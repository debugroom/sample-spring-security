# Dockerfile for sample service using embedded tomcat server

FROM centos:centos7
MAINTAINER debugroom

RUN yum install -y \
    java-11-openjdk \
    java-11-openjdk-devel \
    wget tar iproute git

RUN rm -f /etc/rpm/macros.image-language-conf && \
    sed -i '/^override_install_langs=/d' /etc/yum.conf && \
    yum -y update glibc-common && \
    yum clean all

ENV LANG="ja_JP.UTF-8" \
    LANGUAGE="ja_JP:ja" \
    LC_ALL="ja_JP.UTF-8"

RUN wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
RUN sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
RUN yum install -y apache-maven
ENV JAVA_HOME /etc/alternatives/jre_11
RUN git clone https://github.com/debugroom/sample-spring-security.git /usr/local/sample-spring-security
RUN mvn install -f /usr/local/sample-spring-security/common/pom.xml
RUN mvn package -f /usr/local/sample-spring-security/backend-user/pom.xml
RUN cp /etc/localtime /etc/localtime.org
RUN ln -sf  /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

EXPOSE 8080

CMD $JAVA_HOME/bin/java -jar -Dspring.profiles.active=$ENV_TYPE /usr/local/sample-spring-security/backend-user/target/sample-spring-security-backend-user-0.0.1-SNAPSHOT.jar