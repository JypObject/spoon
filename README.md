# spoon 配置环境

## docker环境安装
### docker安装
1. 安装yum-utils：
yum install -y yum-utils \
device-mapper-persistent-data \
lvm2
2. 为yum源添加docker仓库位置：
yum-config-manager \
--add-repo \
https://download.docker.com/linux/centos/docker-ce.repo
3. 安装docker:
yum install docker-ce
4. 启动docker:
systemctl start docker

## mysql安装
### 下载镜像文件
docker pull mysql:5.7
### 创建实例并启动
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root  \
-d mysql:5.7

## redis安装
### 下载镜像文件
docker pull redis:3.2
### 创建实例并启动
docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data -d redis:3.2 redis-server --appendonly yes --requirepass "spoon"

## rabbitmq安装
### 下载镜像文件
docker pull rabbitmq:management
### 创建实例并启动
docker run -d --name rabbitmq --publish 5671:5671 \
 --publish 5672:5672 --publish 4369:4369 --publish 25672:25672 --publish 15671:15671 --publish 15672:15672 \
 -e RABBITMQ_DEFAULT_USER=root -e RABBITMQ_DEFAULT_PASS=root \
rabbitmq:management



# 服务安装地址
## mysql
ip:3306
## rabbitmq
### 连接地址
ip:5672 
### web访问地址
ip:15672 默认用户名/密码 root/root
### redis
ip:6379 密码 spoon
