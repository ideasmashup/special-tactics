# Build configuration

In order to run the AI bot you must compile it to a runnable jar to be launched before starting ChaosLauncher.
For that purpose you can either choose to rely on manual (command-line) building or use your IDE (Eclipse, Netbeans...)

## Manual build

### Oracle Java

Make sure java is installed properly (should be if you followed the [Getting started](install.md) guide), and that you have configured the JAVA_HOME environment variable :

```
JAVA_HOME=C:\Progra~2\Java\jre8
```

**On Windows 64bits systems:** use the shortened paths to avoid having build paths containing whitespaces

```
Progra~1 = 'Program Files'
Progra~2 = 'Program Files(x86)' 
```

*original instructions: https://confluence.atlassian.com/display/DOC/Setting+the+JAVA_HOME+Variable+in+Windows*

### Apache Ant

First download lastest Ant binary package : http://ant.apache.org/bindownload.cgi, extract-it and install it somewhere (e.g. C:\apache-ant-1.9.4)

Set the ANT_HOME environment variable:
```
ANT_HOME=C:\apache-ant-1.9.4
```

Then add the /bin folder to the PATH variable:
```
PATH= ... ;C:\apache-ant-1.9.4\bin
```

*original instructions: http://ant.apache.org/manual/install.html*


## IDE auto-build

Just run your project, then launch ChaosLauncher and start Starcraft.

### Eclipse settings

(complete this page)
