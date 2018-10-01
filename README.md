# EPAY

### Instruciton on Linux (Debian-based distributions)

Here we have 2 ways for deployment instrcution

#### 1. Run in your machine

##### Prerequisites
- Maven
```shell
 $ sudo apt-get install maven
```
First, clone the project
```shell
 $ git clone https://github.com/alisharifi01/epay.git
```
Then change directory to project root
```shell
 $ cd ./epay
```

Use maven spring boot plugin to run project
```shell
 $ mvn spring-boot:run
```
Launch swagger ui http://localhost:8080/swagger-ui.html

 
#### 2. Build as a docker container


##### Prerequisites
- Docker installed. Here is an instruction for Ubuntu 16.04
https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04

- Maven
```shell
 $ sudo apt-get install maven
```

First, clone the project
```shell
 $ git clone https://github.com/alisharifi01/epay.git
```
Then change directory to project root
```shell
 $ cd ./epay
```

Build the project using maven. 
```shell
 $ mvn clean package
```

Then, build docker image using maven dockerfile plugin
```shell
 $ mvn dockerfile:build
```
Run docker image as a container
```shell
 $ docker run -it --network=host ingenico/epay:0.1-SNAPSHOT
 ```
Finally, launch swagger URL http://localhost:8080/swagger-ui.html


### Developers

Ali Sharifi   (alisharifi01@gmail.com)
