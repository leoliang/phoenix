#%define		name	value
%define __jar_repack 0


Name:		phoenix-agent-tomcat
Version:	0.1
Release:	1
Summary:	phoenix-agent-tomcat
Requires:	git
Requires(pre): /usr/sbin/useradd, /usr/bin/getent, /usr/sbin/usermod

Group:		Development/Tools
License:	GPL
Source0:	%{name}-%{version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-XXXXXXXXXX)


%description
A powerful customized J2EE web container (JBoss, Jetty, Tomcat)

%pre
# add user phoenix
/usr/bin/getent passwd phoenix || /usr/sbin/useradd -u 2200 phoenix
/usr/sbin/usermod -a -G nobody phoenix || true

%prep
%setup -q


%build


%install
[ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT/*

# where to install agent files
AGENT_INSTALL_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-agent
AGENT_CONFIG_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-config
BOOTSTRAP_JAR_DIR=$RPM_BUILD_ROOT/usr/local/tomcat/lib/


# create agent directories
[ -d $AGENT_INSTALL_DIR ] || mkdir -p $AGENT_INSTALL_DIR
[ -d $AGENT_CONFIG_DIR ] || mkdir -p $AGENT_CONFIG_DIR
[ -d $BOOTSTRAP_JAR_DIR ] || mkdir -p $BOOTSTRAP_JAR_DIR

# copy agent files to corresponding directories
cp -r phoenix-agent/* $AGENT_INSTALL_DIR/
chmod +x $AGENT_INSTALL_DIR/startup.sh
chmod +x $AGENT_INSTALL_DIR/phoenix-agent
cp config.xml $AGENT_CONFIG_DIR
cp phoenix-bootstrap.jar $BOOTSTRAP_JAR_DIR



%post
# change required file permissions

TOMCAT_SERVER_XML=/usr/local/tomcat/conf/server.xml
TOMCAT_LOCALHOST_CONF_DIR=/usr/local/tomcat/conf/Catalina/localhost/
TOMCAT_BOOTSTRAP_JAR_DIR=/usr/local/tomcat/lib/

APPLOGS_DIR=/data/applogs
APPDATAS_DIR=/data/appdatas
PHOENIX_ROOT_DIR=/data/webapps/phoenix/
[ -d $APPLOGS_DIR ] && chown nobody:nobody $APPLOGS_DIR && chmod 775 $APPLOGS_DIR
[ -d $APPDATAS_DIR ] && chown nobody:nobody $APPDATAS_DIR && chmod 775 $APPDATAS_DIR
[ -d $PHOENIX_ROOT_DIR ] && chown -R phoenix:phoenix $PHOENIX_ROOT_DIR

[ -f $TOMCAT_SERVER_XML ] && /bin/chown phoenix:phoenix $TOMCAT_SERVER_XML
[ -d $TOMCAT_LOCALHOST_CONF_DIR ] && /bin/chown -R phoenix:phoenix $TOMCAT_LOCALHOST_CONF_DIR


# comment out Defaults requiretty to enabel sudo in scripts
awk 'BEGIN{result=""}{if(match($0, "^[^#]*Defaults[[:space:]]+requiretty")>0){result=sprintf("%s#%s\n",result,$0);}else{result=sprintf("%s%s\n",result,$0);}}END{print result > "/etc/sudoers"}' /etc/sudoers


%clean
rm -rf $RPM_BUILD_ROOT


%files
%defattr(-,phoenix,phoenix,-)
/data/webapps/phoenix/phoenix-agent
/data/webapps/phoenix/phoenix-config
%attr(-, root, root) /usr/local/tomcat/lib/phoenix-bootstrap.jar
%doc


%changelog


%preun
/usr/local/jdk/bin/jps -lvm | awk '$2=="com.dianping.phoenix.agent.PhoenixAgent"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'


%postun
/usr/local/jdk/bin/jps -lvm | awk '$2=="com.dianping.phoenix.agent.PhoenixAgent"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
rm -rf /data/webapps/phoenix/phoenix-agent
rm -rf /data/webapps/phoenix/phoenix-config
