# ImageAI 接口文档（前端联调）

## 概览
- 基础地址：http://{host}:{port}（默认 8080）
- 数据格式：除文件上传外为 application/json
- 鉴权：当前无鉴权（生产环境请加固）
- 跨域：已全局 `@CrossOrigin`

## 通用响应结构
```json
{
  "code": "200",
  "info": "成功",
  "data": {}
}
```
- `code`："200" 成功；"500" 失败（HTTP 始终 200，前端需自行判断）
- `info`：描述信息
- `data`：业务数据

## 任务状态
| 状态 | 含义 | 前端建议 |
| --- | --- | --- |
| running | 任务执行中 | 2~5 秒轮询 |
| success | 完成 | 展示结果后停止轮询（避免重复写库） |
| failed | 失败 | 提示重试 |
| null/空 | Redis 未就绪或任务不存在 | 视为不存在/已过期 |

## 接口列表

### 1) 用户登录
- 方法/路径：`POST /image/ai/api/user/login`
- Content-Type：`application/json`
- 请求体：
```json
{ "username": "admin", "password": "123456" }
```
- 响应：`data` 为 `true/false`
- 说明：用户名或密码缺失返回 `code="200", data=false`；密码错误返回 `code="500", data=false`

### 2) 文生图任务
- 方法/路径：`POST /image/ai/api/chat/text_to_image`
- Content-Type：`application/json`
- 请求体：
```json
{
  "prompt": "一只在太空漫步的橘猫",
  "models": ["cogview-3-flash", "doubao-seedream-4-0"],
  "mode": "optional",
  "conversationsId": 1,
  "size": "1024x1024"
}
```
- 字段说明：
  - `prompt`：文案描述，必填
  - `models`：模型列表，必填；目前支持 `cogview-3-flash`（智谱）和 `doubao-seedream-4-0`（豆包）
  - `mode`：保留字段，可忽略
  - `conversationsId`：会话 ID，传入则复用该会话，否则自动创建
  - `size`：图片大小，默认 `1024x1024`
- 响应示例：
```json
{
  "code": "200",
  "info": "成功",
  "data": {
    "taskId": "uuid",
    "conversationsId": 12
  }
}
```

### 3) 任务结果查询
- 方法/路径：`GET /image/ai/api/chat/tasks/{taskId}`
- 响应示例：
```json
{
  "code": "200",
  "info": "成功",
  "data": {
    "status": "running",
    "chatResultEntityList": [
      {
        "imageURL": "http://{domain}/uploads/xxx.png",
        "modelName": "豆包",
        "modelId": "doubao-seedream-4-0",
        "dateTime": "2025-11-26T10:00:00.000+08:00"
      }
    ],
    "conversationsId": null
  }
}
```
- 说明：`status` 为上表状态；成功后会自动写入消息表。

### 4) 图生图任务
- 方法/路径：`POST /image/ai/api/chat/image_to_image`
- Content-Type：`multipart/form-data`
- 表单字段：
  - `chatRequestDTO`：JSON 字符串，结构同文生图请求
  - `imageFile`：上传图片文件
- 响应：与文生图一致（`data` 返回 `taskId`）
- 说明：后端会先调用豆包多模态模型生成图片描述，再拼接原 prompt 发起文生图。

### 5) 消息列表
- 方法/路径：`GET /image/ai/api/message/list/{conversationsId}`
- 响应示例：
```json
{
  "code": "200",
  "info": "成功",
  "data": [
    {
      "title": "一只在太空漫步的橘猫",
      "content": "一只在太空漫步的橘猫",
      "role": "assistant",
      "createdTime": "2025-11-26 10:00:00",
      "assistantMessages": [
        {
          "modelName": "豆包",
          "modelId": "doubao-seedream-4-0",
          "imageURL": "http://{domain}/uploads/xxx.png"
        }
      ]
    }
  ]
}
```
- 说明：当前仅返回包含图片的消息记录。

### 6) 会话列表
- 方法/路径：`GET /image/ai/api/message/conversations_list`
- 响应示例：
```json
{
  "code": "200",
  "info": "成功",
  "data": [
    { "id": 12, "title": "一只在太空漫步的橘猫", "createdTime": "2025-11-26 10:00:00" }
  ]
}
```

## 其他说明
- 图片访问：接口返回的 `imageURL` 指向 `http://{server.domain}/uploads/{filename}`。
- 上传限制：单文件与总请求均默认 20MB（见 `spring.servlet.multipart` 配置）。
