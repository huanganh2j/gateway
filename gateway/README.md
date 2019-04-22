
## 简单说明

* 此服务为网关服务： 主要提供过滤，鉴权，限流，分发，负载均衡，重定向等作用。

## 客户端


### 说明

* 登录成功后，将token信息存储到本地，每次请求在header 里面带上token ,头名称为token.
* 注销时，删除本地token信息.跳转页面(产品沟通).

### 鉴权错误码

```
10300 未授权访问!
10301 身份验证失败,请输入正确信息!
10302 TOKEN 已失效，刷新token或者重新获取!


具体错误展现，请和产品沟通.  
```  

### 流程图

![登录鉴权](http://192.168.0.238/bitell/server/gateway/uploads/0ac219a15e24ed2828cb8154c160e3f9/denglujianquan.jpg)

##后端


### 访问路径: http://localhost:8080/${application.name}/${path}

* ${application.name}  要调用的服务的spring.application.name
* ${path} 调用的服务路径


### 路径区分

#### 所有需要登录鉴权才能访问的路径，统一采用以"/auth"结尾的路径。

```
比如:   /example-api/login  不用登录鉴权
       /example-api/list  需要用户信息但是不用鉴权。
       /example-api/user/auth  需要鉴权才能访问。


```

#### 各业务系统如何获取user_id 信息

* 从header 里面获取,名称为"BITELL_USER_ID"。或者直接调用框架中的变量CommonStaticConstant.HEADER_USER_ID

