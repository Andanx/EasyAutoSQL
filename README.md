```text
    ______                    _____ ____    __ 
   / ____/___ ________  __   / ___// __ \  / / 
  / __/ / __ `/ ___/ / / /   \__ \/ / / / / /  
 / /___/ /_/ (__  ) /_/ /   ___/ / /_/ / / /___
/_____/\__,_/____/\__, /   /____/\___\_\/_____/
                 /____/                        
```

# EasySQL

[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/easysql/badge)](https://www.codefactor.io/repository/github/carmjos/easysql)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/EasySQL)
[![License](https://img.shields.io/github/license/CarmJos/EasySQL)](https://opensource.org/licenses/GPL-3.0)
[![workflow](https://github.com/CarmJos/EasySQL/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/EasySQL/actions/workflows/maven.yml)
![](https://visitor-badge.glitch.me/badge?page_id=EasySQL.readme)

简单便捷的数据库操作工具，可自定义连接池来源。

随项目分别提供 [BeeCP](https://github.com/Chris2018998/BeeCP) 与 [Hikari](https://github.com/brettwooldridge/HikariCP~~~~)
两个连接池的版本。

## 优势

- 基于JDBC开发，可自选连接池、JDBC驱动。
- 简单便捷的增删改查接口，无需手写SQL语句。
- 额外提供部分常用情况的SQL操作
    - 存在则更新，不存在则插入
    - 创建表
    - 修改表
    - ...
- 支持同步操作与异步操作

## 开发

详细开发介绍请 [点击这里](.documentation/INDEX.md) , JavaDoc(最新Release) 请 [点击这里](https://carmjos.github.io/EasySQL) 。

### 示例代码

```java
public class EasySQLDemo {

	public void createTable(SQLManager sqlManager) {
		//异步创建表
		sqlManager.createTable("users")
				.addColumn("id", "INT(11) AUTO_INCREMENT NOT NULL PRIMARY KEY")
				.addColumn("username", "VARCHAR(16) NOT NULL UNIQUE KEY")
				.addColumn("age", "INT(3) NOT NULL DEFAULT 1")
				.addColumn("email", "VARCHAR(32)")
				.addColumn("phone", "VARCHAR(16)")
				.addColumn("registerTime", "DATETIME NOT NULL")
				.build().execute(null /* 不处理错误 */);
	}

	public void sqlQuery(SQLManager sqlManager) {
		// 同步SQL查询
		try (SQLQuery query = sqlManager.createQuery()
				.inTable("users") // 在users表中查询
				.selectColumn("id", "name") // 选中 id 与 name列~~~~
				.addCondition("age", ">", 18) // 限定 age 要大于5
				.addCondition("email", null) // 限定查询 email 字段为空
				.addNotNullCondition("phone") // 限定 phone 字段不为空
				.addTimeCondition("registerTime", // 时间字段
						System.currentTimeMillis() - 100000, //限制开始时间
						-1//不限制结束时间
				).build().execute()) {
			ResultSet resultSet = query.getResultSet();
			//do something

		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public void sqlQueryAsync(SQLManager sqlManager) {
		// 异步SQL查询
		sqlManager.createQuery()
				.inTable("users") // 在users表中查询
				.addCondition("id", 5) // 限定 id 为 5
				.setLimit(1).build().executeAsync(success -> {
					ResultSet resultSet = success.getResultSet();
					//do something
				}, exception -> {
					//do something
				});
	}

	public void sqlInsert(SQLManager sqlManager) {
		// 同步SQL插入 （不使用try-catch的情况下，返回的数值可能为空。）
		Integer id = sqlManager.createInsert("users")
				.setColumnNames("username", "phone", "email", "registerTime")
				.setParams("CarmJos", "18888888888", "carm@carm.cc", TimeDateUtils.getCurrentTime())
				.setKeyIndex(1) // 设定自增主键的index，将会在后续返回自增主键
				.execute(exception -> {
					// 处理异常
				});
	}

}
```

更多演示详见开发介绍。

### 依赖方式 (Maven)

```xml

<project>
    <repositories>
        <repository>
            <id>github</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/EasySQL</url>
        </repository>
    </repositories>
    <dependencies>
        <!--对于需要提供公共接口的项目，可以仅打包API部分，方便他人调用-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easysql-api</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--如需自定义连接池，则可以仅打包实现部分，自行创建SQLManager-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easysql-impl</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--如需自定义连接池，则可以仅打包实现部分，自行创建SQLManager-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easysql-beecp</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--也可直接选择打包了连接池的版本-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easysql-beecp</artifactId>
            <version>[LATEST VERSION]</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easysql-hikaricp</artifactId>
            <version>[LATEST VERSION]</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>
</project>

```

## 支持与捐赠

若您觉得本插件做的不错，您可以通过捐赠支持我！

感谢您对开源项目的支持！

<img height=25% width=25% src="https://raw.githubusercontent.com/CarmJos/CarmJos/main/img/donate-code.jpg"  alt=""/>

## 开源协议

本项目源码采用 [GNU General Public License v3.0](https://opensource.org/licenses/GPL-3.0) 开源协议。
> ### 关于 GPL 协议
> GNU General Public Licence (GPL) 有可能是开源界最常用的许可模式。GPL 保证了所有开发者的权利，同时为使用者提供了足够的复制，分发，修改的权利：
>
> #### 可自由复制
> 你可以将软件复制到你的电脑，你客户的电脑，或者任何地方。复制份数没有任何限制。
> #### 可自由分发
> 在你的网站提供下载，拷贝到U盘送人，或者将源代码打印出来从窗户扔出去（环保起见，请别这样做）。
> #### 可以用来盈利
> 你可以在分发软件的时候收费，但你必须在收费前向你的客户提供该软件的 GNU GPL 许可协议，以便让他们知道，他们可以从别的渠道免费得到这份软件，以及你收费的理由。
> #### 可自由修改
> 如果你想添加或删除某个功能，没问题，如果你想在别的项目中使用部分代码，也没问题，唯一的要求是，使用了这段代码的项目也必须使用 GPL 协议。
>
> 需要注意的是，分发的时候，需要明确提供源代码和二进制文件，另外，用于某些程序的某些协议有一些问题和限制，你可以看一下 @PierreJoye 写的 Practical Guide to GPL Compliance 一文。使用 GPL 协议，你必须在源代码代码中包含相应信息，以及协议本身。
>
> *以上文字来自 [五种开源协议GPL,LGPL,BSD,MIT,Apache](https://www.oschina.net/question/54100_9455) 。*
