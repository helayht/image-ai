# ImageAI 接口文档

## 概览

- **服务名称**：ImageAI
- **基础地址**：`http://{host}:{port}`（默认端口 `8080`，见 `application.yml`）
- **数据格式**：请求与响应均为 `application/json`
- **认证方式**：当前接口未启用额外认证，需自行根据部署环境加固
- **跨域策略**：`@CrossOrigin` 放开全部来源，如需限制请在服务端配置

---

## 公共响应结构

所有接口统一返回下述包装结构：

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| `code` | `string` | 业务状态码，`"200"` 表示成功，`"500"` 表示失败 |
| `info` | `string` | 业务描述（成功为 `"成功"`；失败时目前返回 `"500"`，建议前端结合实际情况提示） |
| `data` | `T` | 业务数据载荷；失败场景为 `null` |

> **注意**：当前实现无论成功或失败都会返回 HTTP 200。请以前端逻辑解析 `code` 字段判断业务是否成功。

---

## 任务生命周期

1. 客户端调用 **POST** `/image/ai/api/chat/text_to_image` 提交文本与模型，服务端立即返回一个 `taskId`。
2. 服务端异步调用各模型生成图片，并将任务状态与结果写入 Redis：`task_status_{taskId}` 记录状态，`task_result_{taskId}` 存储生成结果。
3. 客户端使用 **GET** `/image/ai/api/chat/tasks/{taskId}` 轮询任务。

任务状态含义：

| 状态 | 说明 | 处理建议 |
| ---- | ---- | ---- |
| `running` | 仍在生成图片 | 前端可每 2~5 秒重试查询接口，或提供取消按钮 |
| `success` | 所有模型执行成功 | 读取 `chatResultEntityList` 展示图片列表 |
| `failed` | 任务失败，通常是某个模型抛异常 | 结合 `info` 提示用户，可提供重试入口 |

---

## 接口详情

### 1. 提交文生图任务

- **方法**：POST
- **地址**：`/image/ai/api/chat/text_to_image`
- **功能**：根据文本提示异步触发多模型生成任务，返回任务 ID。

#### 请求体

```json
{
  "prompt": "生成一张月亮照片",
  "models": ["cogview-3-flash"],
  "mode": "fast",
  "size": "1024x1024"
}
```

| 字段 | 类型 | 是否必填 | 说明 |
| ---- | ---- | ---- | ---- |
| `prompt` | `string` | 是 | 文本提示词，将直接透传给模型 |
| `models` | `string[]` | 是 | 需要调用的模型列表。缺省或空数组会返回失败。当前默认注册 `cogview-3-flash` |
| `mode` | `string` | 否 | 业务保留字段，后端暂未使用，可传空或保留值 |
| `size` | `string` | 否 | 图片尺寸，默认 `1024x1024`；是否支持其他尺寸取决于底层模型 |

#### 成功响应

```json
{
  "code": "200",
  "info": "成功",
  "data": "38b08f57-3a50-4065-92a2-1ac8e8ecf6e5"
}
```

`data` 字段即任务编号，后续查询接口需要使用它。

#### 失败响应

```json
{
  "code": "500",
  "info": "500",
  "data": null
}
```

常见失败原因：

| 场景 | 说明 | 前端建议 |
| ---- | ---- | ---- |
| 模型列表为空 | `models` 未传或为空数组 | 在提交前做必填校验 |
| 未知模型 | `models` 中包含未注册的模型 id | 限制下拉选择或调用前校验 |
| 第三方接口异常 | ZhipuAI 服务返回错误或数据结构异常 | 根据 `code`、`info` 及网络状态提示用户重试 |
| 服务端异常 | 其他未捕获异常 | 建议配合日志定位问题 |

---

### 2. 查询任务结果

- **方法**：GET
- **地址**：`/image/ai/api/chat/tasks/{taskId}`
- **功能**：根据任务 ID 查询当前状态与生成结果。

#### 成功响应

```json
{
  "code": "200",
  "info": "成功",
  "data": {
    "status": "success",
    "chatResultEntityList": [
      {
        "imageURL": "https://example.cdn/zhipu/xxx.png",
        "modelName": "智谱清言",
        "modelId": "cogview-3-flash",
        "dateTime": "2025-11-06T14:30:12.345+08:00"
      }
    ]
  }
}
```

响应字段说明：

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| `status` | `string` | 任务状态，取值见前文（`running`/`success`/`failed`） |
| `chatResultEntityList` | `ChatResultEntity[]` | 生成结果列表；`running` 或 `failed` 时可能为空 |

`ChatResultEntity` 结构：

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| `imageURL` | `string` | 生成图片的可访问地址，来自底层模型返回 |
| `modelName` | `string` | 模型中文名 |
| `modelId` | `string` | 模型标识，与提交时的 `models` 值一致 |
| `dateTime` | `string` | 服务器生成时间（Java `Date` 序列化格式，通常为 ISO8601） |

#### 失败响应

接口异常时返回与公共结构一致的失败报文。若 `taskId` 不存在，`status` 和 `chatResultEntityList` 可能为 `null`，请在前端兜底提示“任务不存在或已过期”。

---

## 其它说明

- 后端会读取 `application.yml` 中的 `ai.api-key.chat-glm` 调用 ZhipuAI。请勿在生产环境直接暴露真实密钥，推荐从环境变量或配置中心注入。
- 当前 `ChatController.TextToImage` 方法名非驼峰写法，返回的失败信息较为笼统。如需更细粒度的错误码或日志，可与后端开发进一步约定。
- 测试类 `Test.java` 会触发真实 API 调用，联调或持续集成时请谨慎执行。

如需补充更多场景（例如多模型并发生成、异步轮询策略等），欢迎继续告知。
