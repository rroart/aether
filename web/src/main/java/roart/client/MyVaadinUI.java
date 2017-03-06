package roart.client;

import roart.lang.LanguageDetect;
import roart.model.IndexFiles;
import roart.model.ResultItem;
import roart.model.FileObject;
import roart.model.SearchDisplay;
import roart.thread.ClientRunner;
import roart.util.Constants;
import roart.zkutil.ZKMessageUtil;
import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.config.NodeConfig;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.service.SearchService;
import roart.service.ControlService;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.Date;
import java.util.TreeSet;
import java.io.File;
import java.io.InputStream;






//import roart.beans.session.misc.Unit;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Alignment;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.Window;
import com.vaadin.annotations.Push;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.shared.ui.label.ContentMode;






//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Push
//@Theme("mytheme")
@Theme("valo")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI
{

    private Logger log = LoggerFactory.getLogger(this.getClass());
    //private static final Logger log = LoggerFactory.getLogger(MyVaadinUI.class);

    public static Map<String, UI> uis = new HashMap<String, UI>();

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "roart.client.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private void tryLogin(String user, String password) { 
	if (Constants.USER.equals(user) && Constants.USER.equals(password)) {
	    getSession().setAttribute(Constants.USER, Constants.USER);
	}
	if (Constants.ADMIN.equals(user) && Constants.ADMIN.equals(password)) {
	    getSession().setAttribute(Constants.USER, Constants.ADMIN);
	}
	initVars();
	setVisibilities();
    } 

    private void initVars() {
	boolean dodownload = MyConfig.conf.downloader;

	boolean doauthenticate = MyConfig.conf.authenticate;
	getSession().setAttribute(ConfigConstants.AUTHENTICATE, doauthenticate);

	boolean accessAdmin = false;
	boolean accessUser = false;
	String addr = (String) getSession().getAttribute(Constants.ADDR);
	accessAdmin = addr.equals("127.0.0.1");
	//accessUser = addr.equals("127.0.0.1");
	if (accessUser) {
	    getSession().setAttribute(Constants.USER, Constants.USER);
	}
	if (accessAdmin) {
	    getSession().setAttribute(Constants.USER, Constants.ADMIN);
	}

	boolean userNone = Constants.NONE.equals(getSession().getAttribute(Constants.USER));
	boolean userUser = Constants.USER.equals(getSession().getAttribute(Constants.USER));
	boolean userAdmin = Constants.ADMIN.equals(getSession().getAttribute(Constants.USER));

	accessUser |= !userNone;

	dodownload = accessUser && dodownload;
	getSession().setAttribute("download", dodownload);

	//getSession().setAttribute("accessadmin", accessAdmin);
	//getSession().setAttribute("accessuser", accessUser);
    }

    private void setVisibilities()  {
	if ((boolean) getSession().getAttribute("authenticate")) {
	    Button login = (Button) getSession().getAttribute("login");
	    Button logout = (Button) getSession().getAttribute("logout");
	    if (!getSession().getAttribute(Constants.USER).equals(Constants.NONE)) {
		login.setVisible(false);
		logout.setVisible(true);
	    } else {
		logout.setVisible(false);
		login.setVisible(true);
	    }
	}
	VerticalLayout cpTab = (VerticalLayout) getSession().getAttribute("controlpanel");
    VerticalLayout cnfTab = (VerticalLayout) getSession().getAttribute("config");
	if (Constants.ADMIN.equals(getSession().getAttribute(Constants.USER))) {
	    cpTab.setVisible(true);
        cnfTab.setVisible(true);
	    statLabel.setVisible(true);
	    ClientRunner.uiset.putIfAbsent(this, "value");
	} else {
	    cpTab.setVisible(false);
        cnfTab.setVisible(false);
	    statLabel.setVisible(false);
	    ClientRunner.uiset.remove(this, "value");
	}
	VerticalLayout sTab = (VerticalLayout) getSession().getAttribute("search");
	if (!Constants.NONE.equals(getSession().getAttribute(Constants.USER))) {
	    sTab.setVisible(true);
	} else {
	    sTab.setVisible(false);
	}
    }

    private TabSheet tabsheet = null;
    public Label statLabel = null;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
	VerticalLayout searchTab = null, controlPanelTab = null, configTab = null;

	com.vaadin.server.Page.getCurrent().setTitle("Aether disk search engine by Roar Thronæs");

        layout.setMargin(true);
        setContent(layout);

	getSession().setAttribute("addr", request.getRemoteAddr());
	getSession().setAttribute(Constants.USER, Constants.NONE);
	initVars();
        
	HorizontalLayout topLine = new HorizontalLayout();
	Label topTitle = new Label("Aether disk search engine");
	topTitle.setWidth("90%");
	topLine.addComponent(topTitle);
	topLine.setHeight("10%");
	topLine.setWidth("100%");	
	boolean doauthenticate = (boolean) getSession().getAttribute("authenticate");
	statLabel = new Label("", ContentMode.PREFORMATTED);
	statLabel.setWidth("50%");
	topLine.addComponent(statLabel);
	if (doauthenticate) {
	    Button login = getLoginButton();
	    Button logout = getLogoutButton();
	    topLine.addComponent(login);
	    topLine.addComponent(logout);
	    getSession().setAttribute("login", login);
	    getSession().setAttribute("logout", logout);
	}
	layout.addComponent(topLine);

	tabsheet = new TabSheet();
	tabsheet.setHeight("80%");
	layout.addComponent(tabsheet);
	// Create the first tab
	searchTab = getSearchTab();
	getSession().setAttribute("search", searchTab);
	// This tab gets its caption from the component caption
	controlPanelTab = getControlPanelTab();
	getSession().setAttribute("controlpanel", controlPanelTab);
    // This tab gets its caption from the component caption
    configTab = getConfigTab(ControlService.nodename);
    getSession().setAttribute("config", configTab);

	tabsheet.addTab(searchTab);
	// This tab gets its caption from the component caption
	tabsheet.addTab(controlPanelTab);
	//tabsheet.addTab(statTab);
    tabsheet.addTab(configTab);

	/*
	tabsheet.addTab(miscTab);
	tabsheet.addTab(comicsTab);
	tabsheet.addTab(trainingTab);
	*/

	HorizontalLayout bottomLine = new HorizontalLayout();
	bottomLine.setHeight("10%");
	bottomLine.setWidth("90%");
	Label nodeLabel = new Label("Node " + ControlService.nodename);
	bottomLine.addComponent(nodeLabel);
	Label dbLabel = new Label("Db type " + MyConfig.conf.db);
	//dbLabel.setWidth("30%");
	bottomLine.addComponent(dbLabel);
	Label idxLabel = new Label("Index type " + MyConfig.conf.index);
	//idxLabel.setWidth("30%");
	bottomLine.addComponent(idxLabel);
	Label licenseLabel = new Label("Affero GPL");
	//licenseLabel.setWidth("30%");
	bottomLine.addComponent(licenseLabel);
	//bottomLine.setComponentAlignment(licenseLabel, Alignment.BOTTOM_RIGHT);
	layout.addComponent(bottomLine);
	setVisibilities();
    }

    private VerticalLayout getSearchTab() {
	VerticalLayout tab = new VerticalLayout();
	//tab.addComponent(tf);
	tab.setCaption("Search");
	String myindex = MyConfig.conf.index;
	// TODO make OO of this
	if (myindex.equals(ConfigConstants.LUCENE)) {
	tab.addComponent(getSearch("Search standard", 0));
	tab.addComponent(getSearch("Search analyzing", 1));
	tab.addComponent(getSearch("Search complexphrase", 2));
	tab.addComponent(getSearch("Search extendable", 3));
	tab.addComponent(getSearch("Search multi", 4));
	tab.addComponent(getSearch("Search surround", 5));
	tab.addComponent(getSearch("Search classic", 6));
	tab.addComponent(getSearch("Search simple", 7));
	}
	if (myindex.equals(ConfigConstants.SOLR)) {
	tab.addComponent(getSearch("Search default", 0));
	tab.addComponent(getSearch("Search lucene", 1));
	tab.addComponent(getSearch("Search complexphrase", 2));
	tab.addComponent(getSearch("Search surround", 3));
	tab.addComponent(getSearch("Search simple", 4));
	}
	if (myindex.equals(ConfigConstants.ELASTIC)) {
	tab.addComponent(getSearch("Search", 0));
	}
	return tab;
    }

    private VerticalLayout getControlPanelTab() {
	VerticalLayout tab = new VerticalLayout();
	tab.setCaption("Control Panel");
	HorizontalLayout horNewInd = new HorizontalLayout();
	horNewInd.setHeight("20%");
	horNewInd.setWidth("90%");
	Button fsIndexNew = getFsIndexNew();
	horNewInd.addComponent(fsIndexNew);
	horNewInd.setComponentAlignment(fsIndexNew, Alignment.BOTTOM_LEFT);
	horNewInd.addComponent(getFsIndexNewPath());
	horNewInd.addComponent(getFsIndexNewMd5());
	HorizontalLayout horNew = new HorizontalLayout();
	horNew.setHeight("20%");
	horNew.setWidth("60%");
	Button fsAddNew = getFsAddNew();
	horNew.addComponent(fsAddNew);
	horNew.setComponentAlignment(fsAddNew, Alignment.BOTTOM_LEFT);
	horNew.addComponent(getFsAddNewPath());
	HorizontalLayout horInd = new HorizontalLayout();
	horInd.setHeight("20%");
	horInd.setWidth("90%");
	Button indexNew = getIndexNew();
	horInd.addComponent(indexNew);
	horInd.setComponentAlignment(indexNew, Alignment.BOTTOM_LEFT);
	horInd.addComponent(getIndexNewPath());
	HorizontalLayout horIndSuf = new HorizontalLayout();
	horIndSuf.setHeight("20%");
	horIndSuf.setWidth("90%");
	horIndSuf.addComponent(getIndexSuffix());
	horIndSuf.addComponent(getReindexSuffix());
	HorizontalLayout horReindex = new HorizontalLayout();
	horReindex.setHeight("20%");
	horReindex.setWidth("90%");
	horReindex.addComponent(getReindex());
	horReindex.addComponent(getReindexDateLower());
	horReindex.addComponent(getReindexDateHigher());
	HorizontalLayout horReindex2 = new HorizontalLayout();
	horReindex2.setHeight("20%");
	horReindex2.setWidth("90%");
	horReindex2.addComponent(getReindexLanguage());
	horReindex2.addComponent(getReindexConfiguredLanguage());
	HorizontalLayout horClean = new HorizontalLayout();
	horClean.setHeight("20%");
	horClean.setWidth("60%");
	horClean.addComponent(getConsistent());
	horClean.addComponent(getConsistentCleanup());
    HorizontalLayout horDelete = new HorizontalLayout();
    horDelete.setHeight("20%");
    horDelete.setWidth("60%");
    horDelete.addComponent(getDelete());
    //horDelete.addComponent(getConsistentCleanup());
	HorizontalLayout horStat = new HorizontalLayout();
	horStat.setHeight("20%");
	horStat.setWidth("90%");
	horStat.addComponent(getNotIndexed());
	horStat.addComponent(getMemoryUsage());
	horStat.addComponent(getOverlapping());
	HorizontalLayout horDb = new HorizontalLayout();
	horDb.setHeight("20%");
	horDb.setWidth("60%");
	horDb.addComponent(getDbItem());
	horDb.addComponent(getDbSearch());

    HorizontalLayout horConf = new HorizontalLayout();
    horConf.setHeight("20%");
    horConf.setWidth("60%");
	horConf.addComponent(getConfigNodes());
	
	tab.addComponent(horNewInd);
	tab.addComponent(horNew);
	tab.addComponent(horInd);
	tab.addComponent(horIndSuf);
	tab.addComponent(horReindex);
	tab.addComponent(horReindex2);
	tab.addComponent(horDelete);
	tab.addComponent(horClean);
	tab.addComponent(horStat);
	tab.addComponent(horDb);
	tab.addComponent(horConf);
	return tab;
    }

    private String stringify(String[] strs) {
        String ret = "";
        if (strs != null) {
            for (String str : strs) {
                ret = ret + str + " ";
            }
        }
        return ret;
    }
    
    public Map<String, Component> nodeTabMap = new HashMap<String, Component>();

    public void replace() {
	for (String node : nodeTabMap.keySet()) {
	    Component oldComponent = nodeTabMap.get(node);
	    Component newComponent = getConfigTab(node);
	    tabsheet.replaceComponent(oldComponent, newComponent);
	    if (ControlService.nodename.equals(node)) {
		getSession().setAttribute("config", newComponent);
	    }
	    nodeTabMap.put(node, newComponent);
	}
	Component oldControlPanelTab = (Component) getSession().getAttribute("controlpanel");
	Component newControlPanelTab = getControlPanelTab();
	tabsheet.replaceComponent(oldControlPanelTab, newControlPanelTab);
        getSession().setAttribute("controlpanel", newControlPanelTab);
    }

    private VerticalLayout getConfigTab(String nodename) {
        String DELIMITER = " = ";
        NodeConfig config = MyConfig.instance().getNode(nodename);
        
    VerticalLayout tab = new VerticalLayout();
    nodeTabMap.put(nodename, tab);
    tab.setCaption("Configuration");

    HorizontalLayout name = new HorizontalLayout();
    Label nameLabel = new Label(ConfigConstants.NODENAME + DELIMITER + nodename);    
    tab.addComponent(nameLabel);
    Label dirLabel = new Label(ConfigConstants.DIRLIST + DELIMITER + stringify(config.dirlist));    
    tab.addComponent(dirLabel);
    if (config.dirlistnot != null) {
    Label dirnotLabel = new Label(ConfigConstants.DIRLISTNOT + DELIMITER + stringify(config.dirlistnot));    
    tab.addComponent(dirnotLabel);
    }
    
    VerticalLayout indexConfig = new VerticalLayout();
    indexConfig.setCaption("Indexing");
    Label idxLabel = new Label("Index type " + config.index);
    indexConfig.addComponent(idxLabel);
    if (config.index.equals(ConfigConstants.LUCENE)) {
        Label pathLabel = new Label("Lucene path " + config.lucenepath);
        indexConfig.addComponent(pathLabel);
    }
    if (config.index.equals(ConfigConstants.SOLR)) {
        Label solrLabel = new Label("Solr URL " + config.solrurl); 
        indexConfig.addComponent(solrLabel);
    }
    Label mltLabel = new Label(ConfigConstants.HIGHLIGHTMLT + DELIMITER + config.highlightmlt);
    tab.addComponent(indexConfig);    
    
    HorizontalLayout horConfig = new HorizontalLayout();
    horConfig.setCaption("Indexing parameters");
    horConfig.setHeight("20%");
    horConfig.setWidth("60%");
    horConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.FAILEDLIMIT));
    horConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.INDEXLIMIT));
    horConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.REINDEXLIMIT));
    horConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.TIKATIMEOUT));
    horConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.OTHERTIMEOUT));

    tab.addComponent(horConfig);
    if (config.highlightmlt) {
        HorizontalLayout mltConfig = new HorizontalLayout();
        mltConfig.setCaption("Searching for MoreLikeThis");
        mltConfig.setHeight("20%");
        mltConfig.setWidth("60%");
        mltConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.MLTCOUNT));
        mltConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.MLTMINTF));
        mltConfig.addComponent(getConfigValue(nodename, config, MyConfig.Config.MLTMINDF));
        tab.addComponent(mltConfig);
    }

    HorizontalLayout dbConfig = new HorizontalLayout();
    Label dbLabel = new Label("Db type " + config.db);
    dbConfig.addComponent(dbLabel);
    
    if (config.db.equals(ConfigConstants.HBASE)) {
        VerticalLayout hbaseConfig = new VerticalLayout();
        hbaseConfig.setCaption("Hbase settings");
        hbaseConfig.setHeight("20%");
        hbaseConfig.setWidth("60%");
        Label quorumLabel = new Label(ConfigConstants.HBASEQUORUM + DELIMITER + config.hbasequorum);
        Label portLabel = new Label(ConfigConstants.HBASEPORT + DELIMITER + config.hbaseport);
        Label masterLabel = new Label(ConfigConstants.HBASEMASTER + DELIMITER + config.hbasemaster);
        hbaseConfig.addComponent(quorumLabel);
        hbaseConfig.addComponent(portLabel);
        hbaseConfig.addComponent(masterLabel);
    }
    tab.addComponent(dbConfig);

    VerticalLayout cloudConfig = new VerticalLayout();
    cloudConfig.setCaption("Misc cloud or distributed settings");
    if (config.hdfsdefaultname != null) {
    	if (config.hdfsdefaultname.contains("hdfs")) {
    Label hdfsLabel = new Label(ConfigConstants.HDFSCONFFS + DELIMITER + config.hdfsdefaultname);
    cloudConfig.addComponent(hdfsLabel);
    	} else {
    		   Label swiftUrlLabel = new Label(ConfigConstants.SWIFTCONFURL + DELIMITER + config.swifturl);
    		   Label swiftUserLabel = new Label(ConfigConstants.SWIFTCONFUSER + DELIMITER + config.swiftuser);
    		   Label swiftKeyLabel = new Label(ConfigConstants.SWIFTCONFKEY + DELIMITER + config.swiftkey);
    		   cloudConfig.addComponent(swiftUrlLabel);
    		   cloudConfig.addComponent(swiftUserLabel);
    		   cloudConfig.addComponent(swiftKeyLabel);
    	}
    }
    if (config.zookeeper != null) {
    Label zooLabel = new Label(ConfigConstants.ZOOKEEPER + DELIMITER + config.zookeeper);
    cloudConfig.addComponent(zooLabel);
    }
    Label lockmodeLabel = new Label(ConfigConstants.DISTRIBUTEDLOCKMODE + DELIMITER + config.zookeepersmall);
    cloudConfig.addComponent(lockmodeLabel);
    Label distprocLabel = new Label(ConfigConstants.DISTRIBUTEDPROCESS + DELIMITER + config.distributedtraverse);
    cloudConfig.addComponent(distprocLabel);
    if (config.locker != null) {
    Label lockerLabel = new Label("locker" + DELIMITER + config.locker);
    cloudConfig.addComponent(lockerLabel);
    }
    tab.addComponent(cloudConfig);
    
    if (config.classify != null) {
        VerticalLayout classifyConfig = new VerticalLayout();
        Label classifyLabel = new Label(ConfigConstants.CLASSIFY + DELIMITER + config.classify);
        classifyConfig.addComponent(classifyLabel);
        if (config.classify.equals(ConfigConstants.OPENNLP)) {
            Label pathLabel = new Label(ConfigConstants.OPENNLPMODELPATH + DELIMITER + config.opennlpmodelpath);
            classifyConfig.addComponent(pathLabel);
        }
        if (config.classify.equals(ConfigConstants.MAHOUT) || config.classify.equals(ConfigConstants.MAHOUTSPARK)) {
            classifyConfig.setCaption("Mahout settings");
            Label algorithmLabel = new Label(ConfigConstants.MAHOUTALGORITHM + DELIMITER + config.mahoutalgorithm);
            classifyConfig.addComponent(algorithmLabel);
            if (config.mahoutbasepath != null) {
            Label basepathLabel = new Label(ConfigConstants.MAHOUTBASEPATH + DELIMITER + config.mahoutbasepath);
            classifyConfig.addComponent(basepathLabel);
            }
            if (config.mahoutconffs != null) {
            Label conffsLabel = new Label(ConfigConstants.MAHOUTCONFFS + DELIMITER + config.mahoutconffs);
            classifyConfig.addComponent(conffsLabel);
            }
            Label dictpathLabel = new Label(ConfigConstants.MAHOUTDICTIONARYPATH + DELIMITER + config.mahoutdictionarypath);
            classifyConfig.addComponent(dictpathLabel);
            Label docfreqpathLabel = new Label(ConfigConstants.MAHOUTDOCUMENTFREQUENCYPATH + DELIMITER + config.mahoutdocumentfrequencypath);
            classifyConfig.addComponent(docfreqpathLabel);
            Label labelpathLabel = new Label(ConfigConstants.MAHOUTLABELINDEXFILEPATH + DELIMITER + config.mahoutlabelindexpath);
            classifyConfig.addComponent(labelpathLabel);
            if (config.classify.equals(ConfigConstants.MAHOUT)) {
                Label modelpathLabel = new Label(ConfigConstants.MAHOUTMODELPATH + DELIMITER + config.mahoutmodelpath);               
                classifyConfig.addComponent(modelpathLabel);
            } else {
                Label sparkMaster = new Label(ConfigConstants.MAHOUTSPARKMASTER + DELIMITER + config.mahoutsparkmaster);
                classifyConfig.addComponent(sparkMaster);
            }
      }
        if (config.classify.equals(ConfigConstants.SPARKML)) {
            classifyConfig.setCaption("Spark ML settings");
            Label modelpathLabel = new Label(ConfigConstants.SPARKMLMODELPATH + DELIMITER + config.sparkmlmodelpath);               
            classifyConfig.addComponent(modelpathLabel);
            Label labelindexpathLabel = new Label(ConfigConstants.SPARKMLLABELINDEXPATH + DELIMITER + config.sparkmllabelindexpath);               
            classifyConfig.addComponent(labelindexpathLabel);
               Label sparkMaster = new Label(ConfigConstants.SPARKMASTER + DELIMITER + config.sparkmaster);
                classifyConfig.addComponent(sparkMaster);
            }
        tab.addComponent(classifyConfig);
    }
    
    VerticalLayout miscConfig = new VerticalLayout();
    miscConfig.setCaption("Misc settings");
    Label downloadLabel = new Label(ConfigConstants.DOWNLOADER + DELIMITER + config.downloader);
    miscConfig.addComponent(downloadLabel);
    Label authLabel = new Label(ConfigConstants.AUTHENTICATE + DELIMITER + config.authenticate);
    miscConfig.addComponent(authLabel);
    tab.addComponent(miscConfig);
    
    return tab;
    }

    private Button getLogoutButton() {
        Button button = new Button("Logout");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		getSession().setAttribute(Constants.USER, Constants.NONE);
		initVars();
		setVisibilities();
            }
        });
	return button;
    }

    private Button getLoginButton() {
        Button button = new Button("Login");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		Window result = getLoginWindow();
		addWindow(result);
            }
        });
	return button;
    }

    private Window getLoginWindow() {
        final Window window = new Window("Login");
	window.setWidth("30%");
	window.setHeight("30%");
	window.center();
	final TextField login = new TextField ( "Username");
	final PasswordField password = new PasswordField ( "Password");
	Button button = new Button("Login");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		tryLogin(login.getValue(), password.getValue());
		window.close();
            }
        });
	VerticalLayout vert = new VerticalLayout();
	vert.addComponent(login);
	vert.addComponent(password);
	vert.addComponent(button);
	window.setContent(vert);
	return window;
    }

    private Button getFsIndexNew() {
        Button button = new Button("Index filesystem new items");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		try {
		    maininst.filesystemlucenenew();
		    Notification.show("Request sent");
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
            }
        });
	return button;
    }

    private Button getFsAddNew() {
        Button button = new Button("Filesystem add new");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		try {
		    maininst.traverse();
		    Notification.show("Request sent");
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
            }
        });
	return button;
    }

    private Button getIndexNew() {
        Button button = new Button("Index non-indexed items");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		try {
		    maininst.index(null, false);
		    Notification.show("Request sent");
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
            }
        });
	return button;
    }

    private TextField getDelete() {
        TextField tf = new TextField("Delete path from db");

        // Handle changes in the value
        tf.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Assuming that the value type is a String
                String value = (String) event.getProperty().getValue();
                // Do something with the value
                ControlService maininst = new ControlService();
                try {
                maininst.deletepathdb(value);
                Notification.show("Request sent");
                } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
                }
            }
            });
        // Fire value changes immediately when the field loses focus
        tf.setImmediate(true);
        return tf;
    }

    private Button getConsistent() {
        Button button = new Button("Get consistency");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		 maininst.consistentclean(false);
		 Notification.show("Request sent");
            }
        });
	return button;
    }

    private Button getConsistentCleanup() {
        Button button = new Button("Get consistency and clean");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		 maininst.consistentclean(true);
		 Notification.show("Request sent");
            }
        });
	return button;
    }

    private Button getMemoryUsage() {
        Button button = new Button("Memory usage");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		 maininst.memoryusage();
		 Notification.show("Request sent");
            }
        });
	return button;
    }

    private Button getNotIndexed() {
        Button button = new Button("Get not yet indexed");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		try {
		    maininst.notindexed();
		    Notification.show("Request sent");
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
            }
        });
	return button;
    }

    private Button getOverlapping() {
        Button button = new Button("Overlapping");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		ControlService maininst = new ControlService();
		maininst.overlapping();
		Notification.show("Request sent");
            }
        });
	return button;
    }

    private Button getSimilar(String text, final String md5) {
        Button button = new Button(text);
	button.setHtmlContentAllowed(true);
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		SearchService maininst = new SearchService();
		maininst.searchsimilar(md5);
		Notification.show("Request sent");
            }
        });
	return button;
    }

    private TextField getFsIndexNewPath() {
	TextField tf = new TextField("Index filesystem new items");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.filesystemlucenenew(value, false);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getFsAddNewPath() {
	TextField tf = new TextField("Filesystem add new");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.traverse(value);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getIndexNewPath() {
	TextField tf = new TextField("Index non-indexed items");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.index(value, false);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getConfigValue(String nodename, NodeConfig conf, final MyConfig.Config config) {
	TextField tf = new TextField("Set " + MyConfig.configStrMap.get(config));
	tf.setValue("" + conf.configMap.get(config));
	
	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    try {
		    	Integer i = new Integer(value);
		    	if (i.intValue() < 0) {
		    		throw new NumberFormatException();
		    	}
		    	conf.configMap.put(config, i);
		    	MyConfig.instance().myput(nodename, conf);
		    	Notification.show("Value changed");
		    	ZKMessageUtil.doreconfig();
		    } catch (NumberFormatException e) {
		    	Notification.show("Illegal value, unchanged");
		    	log.error(Constants.EXCEPTION, e);
		    } catch (Exception e) {
		    	log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getCleanup() {
	TextField tf = new TextField("Cleanup");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getCleanup2() {
	TextField tf = new TextField("Cleanup2");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getCleanupfs() {
	TextField tf = new TextField("Cleanupfs");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getReindex() {
	TextField tf = new TextField("Reindex");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.index(value, true);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private InlineDateField getReindexDateLower() {
	InlineDateField tf = new InlineDateField("Reindex on before date");
	// Create a DateField with the default style
	// Set the date and time to present
	Date date = new Date();
	// temp fix
	date.setHours(0);
	date.setMinutes(0);
	date.setSeconds(0);
	tf.setValue(date);

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    Date date = (Date) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    long time = date.getTime();
		    try {
			maininst.reindexdatelower("" + time, true);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private InlineDateField getReindexDateHigher() {
	InlineDateField tf = new InlineDateField("Reindex on after date");
	// Create a DateField with the default style
	// Set the date and time to present
	Date date = new Date();
	// temp fix
	date.setHours(0);
	date.setMinutes(0);
	date.setSeconds(0);
	tf.setValue(date);

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    Date date = (Date) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    long time = date.getTime();
		    try {
			maininst.reindexdatehigher("" + time, true);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getIndexSuffix() {
	TextField tf = new TextField("Index on suffix");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.indexsuffix(value, false);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getReindexSuffix() {
	TextField tf = new TextField("Reindex on suffix");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.indexsuffix(value, true);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private ListSelect getReindexLanguage() {
    	ListSelect ls = new ListSelect("Reindex for language");
    	Set<String> languages = null;
		try {
			Set<String> langs = IndexFilesDao.getLanguages();
			langs.remove(null);
			languages = new TreeSet<String>(langs);
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return ls;
		}
		log.info("languages " + languages);
		if (languages == null ) {
			return ls;
		}
    	ls.addItems(languages);
        ls.setNullSelectionAllowed(false);
        // Show 5 items and a scrollbar if there are more                       
        ls.setRows(5);
        ls.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Assuming that the value type is a String                 
                String value = (String) event.getProperty().getValue();
                // Do something with the value                              
    		    ControlService maininst = new ControlService();
    		    try {
    			maininst.reindexlanguage(value);
    			Notification.show("Request sent");
    		    } catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		    }
    		}
    	    });
    	// Fire value changes immediately when the field loses focus
    	ls.setImmediate(true);
    	return ls;
    }
    
    private ListSelect getReindexConfiguredLanguage() {
    	ListSelect ls = new ListSelect("Reindex for configured language");
    	String[] languages = null;
		try {
			languages = LanguageDetect.getLanguages();
		} catch (Exception e1) {
		}
    	ls.addItems(languages);
        ls.setNullSelectionAllowed(false);
        // Show 5 items and a scrollbar if there are more                       
        ls.setRows(5);
        ls.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Assuming that the value type is a String                 
                String value = (String) event.getProperty().getValue();
                // Do something with the value                              
    		    ControlService maininst = new ControlService();
    		    try {
    			maininst.reindexlanguage(value);
    			Notification.show("Request sent");
    		    } catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		    }
    		}
    	    });
    	// Fire value changes immediately when the field loses focus
    	ls.setImmediate(true);
    	return ls;
    }
    
    private ListSelect getConfigNodes() {
        ListSelect ls = new ListSelect("Get config for node");
        Set<String> nodes = null;
        try {
            Set<String> mynodes = MyConfig.instance().getNodes();
            mynodes.remove(null);
            nodes = new TreeSet<String>(mynodes);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return ls;
        }
        log.info("nodes " + nodes);
        if (nodes == null ) {
            return ls;
        }
        ls.addItems(nodes);
        ls.setNullSelectionAllowed(false);
        // Show 5 items and a scrollbar if there are more                       
        ls.setRows(5);
        ls.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Assuming that the value type is a String                 
                String value = (String) event.getProperty().getValue();
                // Do something with the value                              
                try {
                    VerticalLayout tab = getConfigTab(value);
                    tabsheet.addComponent(tab);
                    tabsheet.getTab(tab).setClosable(true);
                    Notification.show("New configuration available");
                } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
                }
            }
            });
        // Fire value changes immediately when the field loses focus
        ls.setImmediate(true);
        return ls;
    }
    
    private TextField getFsIndexNewMd5() {
	TextField tf = new TextField("Filesystem index on changed md5");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.filesystemlucenenew(value, true);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getDbItem() {
	TextField tf = new TextField("Database md5 id");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.dbindex(value);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getDbSearch() {
	TextField tf = new TextField("Database search");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    ControlService maininst = new ControlService();
		    try {
			maininst.dbsearch(value);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getSearch(String caption, final int type) {
	TextField tf = new TextField(caption);

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
 		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    SearchService maininst = new SearchService();
		    maininst.searchme(value, "" + type);
		    Notification.show("Request sent");
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    void addListTable(VerticalLayout ts, List<ResultItem> strarr) {
	if (strarr.size() <= 1) {
	    return;
	}

	SearchDisplay display = SearchService.getSearchDisplay(getCurrent());
	boolean dodownload = (boolean) getSession().getAttribute("download");

	Table table = new Table("Table");
	table.setWidth("90%");
	int columns = strarr.get(0).get().size();
	int mdcolumn = 0;
	for (int i=0; i<strarr.size(); i++) {
	    if (strarr.get(i).get().size() != columns) {
		System.out.println("column differs " + columns + " found " + strarr.get(i).get().size());
		break;
	    }
	}
	for (int i = 0; i < columns; i++) {
	    if (display.highlightmlt && i == IndexFiles.HIGHLIGHTMLTCOLUMN && strarr.get(0).get().get(IndexFiles.HIGHLIGHTMLTCOLUMN).equals("Highlight and similar")) {
		table.addContainerProperty(strarr.get(0).get().get(i), Button.class, null);
		continue;
	    }
        if (strarr.get(0).get().get(i).equals("Metadata")) {
        table.addContainerProperty(strarr.get(0).get().get(i), Label.class, null);
        mdcolumn = i;
        continue;
        }
	    table.addContainerProperty(strarr.get(0).get().get(i), String.class, null);
	}
	if (dodownload) {
	    if (columns > IndexFiles.FILENAMECOLUMN && strarr.get(0).get().get(IndexFiles.FILENAMECOLUMN).equals("Filename")) {
		table.addGeneratedColumn("Download", new ColumnGenerator() {
@Override
public Object generateCell(Table source, Object itemId,
			   Object columnId) {
    String nodename = (String) source.getItem(itemId).getItemProperty("Node").getValue();
    String filename = (String) source.getItem(itemId).getItemProperty("Filename").getValue();
    if (nodename != null && !nodename.equals(ControlService.nodename)) {
    	return null;
    }
    if (filename == null) {
	return null;
    }
    if (false) {
	return null;
    }
    // TODO make OO of this
    final FileObject fo = FileSystemDao.get(filename);
    if (filename.startsWith(FileSystemDao.FILE) || filename.startsWith(FileSystemDao.HDFS)) {
    	filename = filename.substring(FileSystemDao.FILELEN);
    }
    if (filename.startsWith(FileSystemDao.SWIFT)) {
    	filename = filename.substring(FileSystemDao.SWIFTLEN);
    }
    int i = filename.lastIndexOf("/");
    String fn = filename.substring(i + 1);
    StreamResource resource = new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
                try {
		    return FileSystemDao.getInputStream(fo);
                } catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
                    return null;
                }
            }
	}, fn);
    //FileResource resource = new FileResource(new File(filename));
    Button button = new Button("Download");
    button.setStyleName(BaseTheme.BUTTON_LINK);
    FileDownloader downloader = new FileDownloader(resource);
    downloader.extend(button);
    return button;
}
		    });
	    }
	}
	for (int i = 1; i < strarr.size(); i++) {
	    ResultItem str = strarr.get(i);
	    if (display.highlightmlt && columns > IndexFiles.HIGHLIGHTMLTCOLUMN && strarr.get(0).get().get(IndexFiles.HIGHLIGHTMLTCOLUMN).equals("Highlight and similar")) {
		String text = (String) str.get().get(IndexFiles.HIGHLIGHTMLTCOLUMN);
		if (text != null) {
		String md5 = (String) str.get().get(1);
		str.get().set(IndexFiles.HIGHLIGHTMLTCOLUMN, getSimilar(text, md5));
		}
	    }
	    if (mdcolumn > 0) {
            Label label = null;
	        if (!((String) str.get().get(mdcolumn)).isEmpty()) {
	            label = new Label("MD");
	        } else {
	            label = new Label("No MD");
	        }
	        label.setDescription((String) str.get().get(mdcolumn));
	        str.get().set(mdcolumn, label); 
	        
	    }
	    table.addItem(str.getarr(), i);
	}
	//table.setPageLength(table.size());
	ts.addComponent(table);
    }

    void addList(VerticalLayout ts, List<String> strarr) {
	for (int i=0; i<strarr.size(); i++) {
	    String str = strarr.get(i);
	    ts.addComponent(new Label(str));
	}
    }

    private VerticalLayout getResultTemplate() {
	final Component content = getContent();
	VerticalLayout res = new VerticalLayout();
	res.addComponent(new Label("Search results"));
	/*
        Button button = new Button("Back to main page");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setContent(content);
            }
        });
	res.addComponent(button);
	*/
	return res;
    }

    @SuppressWarnings("rawtypes")
	public void displayResultLists(List<List> lists) {
	VerticalLayout tab = new VerticalLayout();
        tab.setCaption("Search results");

	VerticalLayout result = getResultTemplate();
	if (lists != null) {
	    for (List<ResultItem> list : lists) {
		addListTable(result, list);
	    }
	}
	tab.addComponent(result);

	tabsheet.addComponent(tab);
	tabsheet.getTab(tab).setClosable(true);

	//System.out.println("setcont" +this);
	//getSession().getLockInstance().lock();
	//setContent(result);
	//getSession().getLockInstance().unlock();
    }

    @SuppressWarnings("rawtypes")
	public void displayResultListsTab(List<List> lists) {
	VerticalLayout tab = new VerticalLayout();
        tab.setCaption("Results");

	VerticalLayout result = getResultTemplate();
	if (lists != null) {
	    for (List<ResultItem> list : lists) {
		addListTable(result, list);
	    }
	}
	tab.addComponent(result);

	tabsheet.addComponent(tab);
	tabsheet.getTab(tab).setClosable(true);
	Notification.show("New result available");
    }

    public void notify(String text) {
	Notification.show(text);
    }

}
