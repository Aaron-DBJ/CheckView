# CheckView
具有丰富样式和动画效果的点赞按钮

## 使用方式

  ### 1、Gradle<br>
  在主工程的gradle文件添加
  ```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  然后在项目的gradle文件添加，将tag替换成你所使用的库版本号
  ```gradle
  dependencies {
	        implementation 'com.github.Aaron-DBJ:checkview:tag'
	}
  ```
  ### 2、Maven
  同理先添加jitpack仓库
  ```maven
  <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  ```
  然后添加依赖，将tag替换成你所使用的库版本号
  ```maven
  dependency>
	    <groupId>com.github.Aaron-DBJ</groupId>
	    <artifactId>checkview</artifactId>
	    <version>tag</version>
	</dependency>
  ```
