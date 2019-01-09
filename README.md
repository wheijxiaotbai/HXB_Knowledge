# springboot 集成 jwt security demo

##### 文档说明

该分支为springboor集成jwt security 的demo

##### 如何运行

* postgresql

  >  首先你得准备一个端口为5433的postgresql,用户名和密码为security,db为demo_security
  >
  >
  >
  > 如果你恰巧安装了docker,请使用以下命令在本地构建一个postgresql镜像,参数已经给出,无需进行其他操作
  >
  > run -d --name demo_security --restart always -p 5433:5432 -e TZ=Asia/Shanghai -e POSTGRES_USER=security -e POSTGRES_PASSWORD=security -e POSTGRES_DB=demo_security -v /srv/hxb/postgresql/data:/var/lib/postgresql/data postgres:alpine
  >
  >  
  >
  > 这里之所以postgresql端口配置的为5433是因为demo中的配置文件指定了5433,你可以通过修改demo中的配置文件进行修改

* 通过开发工具打开gradle项目,点击运行即可

* 在浏览器访问127.0.0.1:8080

  ![image](https://github.com/wheijxiaotbai/HXB_Knowledge/blob/springboot_jwt_security_demo/image/demo1_1.png)

* 在demo中没有对创建用户的接口做权限限制,方便自定义用户进行测试

