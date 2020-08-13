FROM mashrequae.azurecr.io/jdk8-jre-hardened:v1
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /opt/appd && \
    mkdir -p /usr/images/wealth-statement-service && \
    chown -R appuser:appgroup /opt/appd && \
    chown -R appuser:appgroup /usr/images/wealth-statement-service

USER appuser

# Do not change any of these
COPY target/*.jar /app.jar
CMD java $JAVA_OPTS \
    -Dappdynamics.jvm.shutdown.mark.node.as.historical=true \
    -Dappdynamics.agent.uniqueHostId=$(sed -rn '1s#.*/##; 1s/(.{12}).*/\1/p' /proc/self/cgroup) \
    -javaagent:/opt/appd/javaagent.jar \
    -jar /app.jar