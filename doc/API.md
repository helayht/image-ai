# ImageAI 接口文档（前端联调）

## 基础信息
- 基础地址：http://{host}:{port}（默认 8080）
- 数据格式：除文件上传外为 application/json
- 跨域：已全局 `@CrossOrigin`

## 鉴权
- 受保护接口：`/image/ai/api/chat/**`、`/image/ai/api/message/**`
- 请求头：`token: <后端签发的 JWT>`（拦截器只看该头部）
- token 过期时间：7 天
- 校验失败/过期时：HTTP 200，响应体类似 `{"state":false,"msg":"token 无效"}` 或 `{"msg":"token 过期"}`，前端需自行跳转登录

### 登录获取 token
- 方法/路径：`POST /image/ai/api/auth/login`
- Content-Type：`application/x-www-form-urlencoded`（或 query 参数）
- 请求参数：`code`（由后端放入 Redis 的验证码，与 openId 绑定）
- 响应示例：
```json
{ "code": "200", "info": "成功", "data": "eyJhbGciOi..." }
```
- 失败时：`code="500"`，`data=null`
- 说明：验证码通过微信公众号交互获得（回复“验证码”），登录成功后保存 token 并在所有受保护接口的请求头携带

## 通用响应结构
```json
{
  "code": "200",
  "info": "成功",
  "data": {}
}
```
- `code`："200" 成功；"500" 失败（HTTP 始终 200）
- `info`：描述信息
- `data`：业务数据

## 任务状态
| 状态 | 含义 | 前端建议 |
| --- | --- | --- |
| running | 任务执行中 | 2~5 秒轮询 |
| success | 完成 | 展示结果后停止轮询（避免重复写库） |
| failed | 失败 | 提示重试 |
| null/空 | Redis 未就绪或任务不存在 | 视为不存在/已过期 |

## 接口列表（需 token 的接口均在路径后标注）

### 1) 文生图任务（需 token）
- 方法/路径：`POST /image/ai/api/chat/text_to_image`
- Headers：`token: <JWT>`
- Content-Type：`application/json`
- 请求体：
```json
{
  "prompt": "一只在太空漫步的橘猫",
  "models": ["cogview-3-flash", "doubao-seedream-4-0"],
  "mode": "optional",
  "conversationsId": 1,
  "size": "1024x1024",
  "username": "demo"
}
```
- 字段说明：
  - `prompt`：文案描述，必填
  - `models`：模型列表，必填；目前支持 `cogview-3-flash`（智谱）和 `doubao-seedream-4-0`（豆包）
  - `mode`：保留字段，可忽略
  - `conversationsId`：会话 ID，传入则复用该会话，否则自动创建
  - `size`：图片大小，默认 `1024x1024`
  - `username`：会话归属人（消息查询需带同一 username）
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

### 2) 任务结果查询（需 token）
- 方法/路径：`GET /image/ai/api/chat/tasks/{taskId}`
- Headers：`token: <JWT>`
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
- 说明：`status` 为上表状态；`success` 时后端会写入消息表。

### 3) 图生图任务（需 token）
- 方法/路径：`POST /image/ai/api/chat/image_to_image`
- Headers：`token: <JWT>`
- Content-Type：`multipart/form-data`
- 表单字段：
  - `chatRequestDTO`：JSON 字符串，结构同文生图请求
  - `imageFile`：上传图片文件
- 响应：同文生图（`data` 返回 `taskId`、`conversationsId`）

### 4) 视频转图任务（需 token）
- 方法/路径：`POST /image/ai/api/chat/vedio_to_image`
- Headers：`token: <JWT>`
- Content-Type：`multipart/form-data`
- 表单字段：
  - `chatRequestDTO`：JSON 字符串，结构同文生图请求
  - `vedioFile`：上传视频文件
- 响应：同文生图

### 5) 消息列表（需 token）
- 方法/路径：`GET /image/ai/api/message/list/{conversationsId}`
- Headers：`token: <JWT>`
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

### 6) 会话列表（需 token）
- 方法/路径：`GET /image/ai/api/message/conversations_list/{username}`
- Headers：`token: <JWT>`
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

### 7) 删除会话（需 token）
- 方法/路径：`POST /image/ai/api/message/conversations/del`
- Headers：`token: <JWT>`
- Content-Type：`application/json`
- 请求体：`[1,2,3]`（会话 ID 列表）
- 响应：`data` 为 `true/false`

### 8) 修改会话标题（需 token）
- 方法/路径：`POST /image/ai/api/message/conversations/update_title`
- Headers：`token: <JWT>`
- Content-Type：`application/json`
- 请求体：
```json
{ "conversationsId": 12, "title": "新的标题", "username": "demo" }
```
- 响应：`data` 为 `true/false`

## 其他说明
- 图片访问：接口返回的 `imageURL` 指向 `http://{server.domain}/uploads/{filename}`。
- 上传限制：单文件与总请求默认 200MB（见 `spring.servlet.multipart` 配置）。
