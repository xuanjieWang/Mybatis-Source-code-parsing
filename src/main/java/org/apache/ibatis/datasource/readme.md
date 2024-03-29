## 数据源
1. 连接池管理：数据源可以维护一组预先创建好的数据库连接，并在需要时将这些连接分配给应用程序。通过使用连接池，可以避免频繁地创建和销毁数据库连接，从而提高性能和降低资源消耗。
2. 连接配置：数据源可以包含连接数据库所需的配置信息，如数据库URL、用户名、密码等。这些配置信息可以在数据源初始化时进行设置，应用程序只需从数据源中获取连接即可，无需重复指定这些配置。
3. 连接管理：数据源负责管理和维护数据库连接的状态。它可以处理连接的分配、回收、超时处理等操作，确保连接的正常运行和高效利用。
4. 高可用性：一些数据源实现支持故障转移和负载均衡，能够在数据库服务器发生故障时自动切换到备用服务器，提高系统的可靠性。

### 在 MyBatis 中，UNPOOLED、POOLED 和 JNDI 是三种不同的数据源类型，用于配置和管理数据库连接。这些数据源类型用于指定如何获取和管理数据库连接，以满足应用程序的需求。
UNPOOLED 数据源：UNPOOLED 数据源是一种简单的数据源实现，每次需要连接时都会创建一个新的数据库连接，并在使用完后关闭。这种数据源适用于小型应用或者单线程环境，它没有连接池的概念，每次操作都需要重新创建连接和释放连接。
POOLED 数据源：POOLED 数据源是通过连接池管理数据库连接的数据源实现。在初始化时，会创建一定数量的数据库连接存入连接池中，当应用程序需要连接时，从连接池中取出一个可用的连接，使用完后归还到连接池中，而不是每次都重新创建和关闭连接。这种数据源适用于多线程环境和高并发应用，可以复用连接，提高性能和效率。
JNDI 数据源：JNDI (Java Naming and Directory Interface) 数据源是通过 JNDI 服务获取数据库连接的数据源实现。JNDI 是 Java 提供的一种命名和目录服务，可以在应用程序中通过名称查找和获取资源，如数据库连接。JNDI 数据源适用于应用程序部署在 Java EE 容器（如 Tomcat、WebLogic 等）中的情况，可以通过容器提供的 JNDI 服务获取数据库连接。
在 MyBatis 的配置文件（mybatis-config.xml）中，可以根据需要选择使用哪种数据源类型，并进行相应的配置。对于 UNPOOLED 和 POOLED 数据源，可以指定相关的 JDBC 驱动、连接 URL、用户名和密码等信息。而对于 JNDI 数据源，需要配置 JNDI 上下文工厂和 JNDI 名称等参数。
使用不同的数据源类型，可以根据应用程序的需求和环境选择最合适的方式管理数据库连接，以提高性能和效率。

#### 一般都是使用数据库连接池
1. HikariCP: HikariCP 是当前性能最好的 JDBC 数据库连接池之一，它具有快速启动、低资源消耗、高性能等特点，被广泛应用于各种Java项目中。
2. Apache Commons DBCP: Apache Commons DBCP 是 Apache 软件基金会提供的一个开源的数据库连接池实现，具有良好的稳定性和性能，被许多项目广泛使用。
3. C3P0: C3P0 是另一个流行的开源 JDBC 数据库连接池，具有丰富的配置选项和扩展性，可以满足各种不同的数据库连接需求。
4. Druid: Druid 是阿里巴巴开源的一个高性能、功能强大的数据库连接池，除了连接池功能外，还提供了监控、统计、防火墙等功能，适用于对连接池有较高要求的项目。
5. Tomcat JDBC Pool: Tomcat JDBC Pool 是 Apache Tomcat 提供的一个简单而高效的 JDBC 连接池实现，适用于与 Tomcat 配合使用的项目。

### 使用动态代理
1. 连接的复用：连接池通过动态代理可以拦截对数据库连接的请求，并在连接可复用时直接返回已有的连接，避免频繁地创建和销毁连接，提高性能和效率。
2. 连接的分配和释放：连接池通过动态代理可以拦截连接的分配和释放操作，确保在使用完毕后将连接归还给连接池，以便其他线程或请求继续使用。
3. 连接的监控和管理：动态代理可以在连接被请求、归还或销毁时进行监控和记录，可以统计连接的使用情况、计算连接的空闲时间等，从而对连接池进行管理和优化。
4. 连接的错误处理和异常处理：动态代理可以捕获连接操作中的异常和错误，例如连接超时、断开等问题，进行相应的处理和恢复，保证连接池的稳定性和可靠性。