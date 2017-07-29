build steps:

1, jdk 1.6.0_12 / 1.6

2, pull git, refresh project

3, update version.txt add comment and release number.

4, under /prod_release/ create release log folder

5, project -> export -> java/JAR file/
    only select: version.txt and src/ folder
    and jar build out to: CSB2BEDICommonCoreLibrary\prod_release\csb2b-edi-common-core-1.0.jar

6, copy 
    version.txt 
    csb2b-edi-common-core-1.0.jar 
    to /20170203/csb2b-edi-common-core-1.0.jar

7, update the jar to mapping project:
    CSB2BEDIGroovyDevelopment\lib

8, push git

9, for local debug, need update to : C:\tibco\bw\5.9\lib

10, in RI, rebuild B2B-APP-MSG-GENERAL-01 and deploy BW ear.
