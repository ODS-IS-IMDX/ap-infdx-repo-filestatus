# © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

##メインステージ
FROM amazoncorretto:21.0.4-al2023

EXPOSE 8080

# 環境変数の設定
ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /usr

# yumリポジトリのリリースバージョン
ARG RELEASE_VER=2023.6.20241212

RUN yum -y update --releasever=${RELEASE_VER} \
    && yum install -y wget \
                       tar \
                       gzip

#Tomcatのインストール
RUN wget -P /usr/local/src \
https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.29/bin/apache-tomcat-10.1.29.tar.gz \
    && tar -zxvf /usr/local/src/apache-tomcat-10.1.29.tar.gz -C /usr/local \
    && rm -rf /usr/local/apache-tomcat-10.1.29/webapps/*

#Tomcatの設定ファイルを差し替え
COPY build-resources/conf/tomcat/server.xml /usr/local/apache-tomcat-10.1.29/conf/server.xml

#Tomcatの起動スクリプトを配置
RUN mkdir /etc/script
COPY build-resources/script/startup.sh /etc/script/startup.sh

#取得ファイル名
ARG TARGET_FILE=infra_external_api_indx_0018*.war

#WARファイルをTomcatのwebappsディレクトリにコピー
COPY target/${TARGET_FILE} /usr/local/apache-tomcat-10.1.29/webapps/ROOT.war

RUN chmod +x /etc/script/startup.sh \
    && chmod +x /usr/local/apache-tomcat-10.1.29/webapps/ROOT.war

#コンテナ起動時にTomcatの起動スクリプトを実行
CMD ["/bin/bash","-c","/etc/script/startup.sh"]
