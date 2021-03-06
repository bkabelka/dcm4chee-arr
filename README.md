dcm4chee-arr
============
Sources: https://github.com/dcm4che/dcm4chee-arr

Issue Tracker: http://www.dcm4che.org/jira/browse/ARR

IHE ATNA-Based audit repository implementation running on JBoss 7 application server and Jboss WildFly.

Features
-----
1. Provides standard repository as defined by IHE Audit Trail
see [ATNA](http://wiki.ihe.net/index.php?title=Audit_Trail_and_Node_Authentication)

  1. Audit messages following the DICOM XML schema for audit messages.

  2. Provides application activity and audit record used message generation.

  3. Uses dcm4che implementation for SYSLOG protocol.

 4. Supports audit reporting via either TLS or UDP.

2. Provides support for LDAP and JSON file configuration

3. Provides a configurable and customizable clean up mechanism for the audit record repository.

4. Provides a configurable and customizable Back up mechanism for the audit record repository.

5. Provides a restful service for controlling the service as well as querying the repository.

6. Provides a simple web interface for filtering queries.

7. Supports new dcm4che library version 3.3.5

8. Supports new dcm4che storage framework v2.0.0

9. Back up mechanism supports exporting tar and zip containers using the dcm4che storage framework.

Build
-----
After installation of [Maven 3](http://maven.apache.org):

   for JSON file config:
 
      mvn install -Ddb={db2|firebird|h2|mysql|oracle|psql|sqlserver}
      
   for ldap config profile:
  
      mvn install -Ddb={db2|firebird|h2|mysql|oracle|psql|sqlserver} -Dldap={apacheds|opends|slapd}

Installation
------------
See [INSTALL.md](https://github.com/dcm4che/dcm4chee-arr/blob/master/INSTALL.md).

License
-------
* [Mozilla Public License Version 1.1](http://www.mozilla.org/MPL/1.1/)

List of Request Parameters for RESTful queries
--------------------------------------
lowerDateTime    
upperDateTime    
eventID    
eventTypeCode  
eventOutcomeIndicator  
eventActionCode  
userID  
alternativeUserID  
userName  
userIsRequestor  
roleIDCode  
networkAccessPointID  
networkAccessPointTypeCode  
auditSourceID  
auditEnterpriseSiteID  
auditSourceTypeCode  
participantObjectID  
participantObjectTypeCode  
participantObjectName  
participantObjectTypeCode  
participantObjectTypeCodeRole  
participantObjectDataLifeCycle  
mostRecentResults  
