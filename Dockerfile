# Use WildFly 10.1.0.Final as the base image
FROM jboss/wildfly:10.1.0.Final

# 1) Copy the WAR file into WildFly’s deployments folder
COPY target/CinePhile.war /opt/jboss/wildfly/standalone/deployments/

# 2) Copy MySQL driver JAR into 
COPY mysql-driver/mysql-connector-java-5.1.49.jar /tmp/

# 3) Copy the CLI script into the WildFly bin directory
COPY configure-datasource.cli /opt/jboss/wildfly/bin/configure-datasource.cli

# 4) Run the CLI script 

RUN /opt/jboss/wildfly/bin/jboss-cli.sh \
        --file=/opt/jboss/wildfly/bin/configure-datasource.cli

# 5) Expose HTTP (8080) and Management (9990) ports
EXPOSE 8080 9990

# 6) Finally, start WildFly in standalone mode, binding to 0.0.0.0
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

