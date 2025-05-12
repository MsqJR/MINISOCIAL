# MINISOCIAL

## WildFly Configuration Setup

### Option 1: Using the provided configuration file
1. Copy the configuration file from the project:
   ```bash
   cp wildfly-config/standalone.xml $WILDFLY_HOME/standalone/configuration/
   ```
2. Restart WildFly server

### Option 2: Manual Configuration using WildFly CLI
If you prefer to keep your existing WildFly configuration, you can just add the required JMS topic:

1. Start WildFly CLI:
   ```bash
   $WILDFLY_HOME/bin/jboss-cli.sh
   ```
2. Connect and add the topic:
   ```bash
   connect
   /subsystem=messaging-activemq/server=default/jms-topic=GroupNotificationTopic:add(entries=["java:/jms/GroupNotificationTopic"])
   reload
   ```

## Verifying the Configuration
1. Access WildFly Admin Console (http://localhost:9990)
2. Navigate to Configuration → Subsystems → Messaging → Server → Topics
3. Verify "GroupNotificationTopic" is listed

MODIFY THE SERVER RUN CONFIGURATIONS IN INTELLIJ:
- goto servies tab -> JBoss/wildfly Server -> <server-name> -> edit configuraiton-> startup/conection
- on startup script uncheck the Use default click on the icon <parameters> beside the checkbox and add -Djboss.server.default.config=standalone-MINISOC.xml to the VM options
