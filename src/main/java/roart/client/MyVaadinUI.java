package roart.client;

import roart.model.ResultItem;
import roart.thread.ClientRunner;

import roart.service.SearchService;
import roart.service.ControlService;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Date;

import java.io.File;

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
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.Window;
import com.vaadin.annotations.Push;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Push
//@Theme("mytheme")
@Theme("valo")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI
{

    private Log log = LogFactory.getLog(this.getClass());
    //private static final Logger log = LoggerFactory.getLogger(MyVaadinUI.class);

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "roart.client.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private void tryLogin(String user, String password) { 
	if ("user".equals(user) && "user".equals(password)) {
	    getSession().setAttribute("user", "user");
	}
	if ("admin".equals(user) && "admin".equals(password)) {
	    getSession().setAttribute("user", "admin");
	}
	initVars();
	setVisibilities();
    } 

    private void initVars() {
	String mydownload = roart.util.Prop.getProp().getProperty("downloader");
	boolean dodownload = mydownload != null && mydownload.length() > 0;


	String myauthenticate = roart.util.Prop.getProp().getProperty("authenticate");
	boolean doauthenticate = myauthenticate != null && myauthenticate.length() > 0;
	getSession().setAttribute("authenticate", doauthenticate);

	boolean accessAdmin = false;
	boolean accessUser = false;
	String addr = (String) getSession().getAttribute("addr");
	accessAdmin = addr.equals("127.0.0.1");
	//accessUser = addr.equals("127.0.0.1");
	if (accessUser) {
	    getSession().setAttribute("user", "user");
	}
	if (accessAdmin) {
	    getSession().setAttribute("user", "admin");
	}

	boolean userNone = "none".equals(getSession().getAttribute("user"));
	boolean userUser = "user".equals(getSession().getAttribute("user"));
	boolean userAdmin = "admin".equals(getSession().getAttribute("user"));

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
	    if (!getSession().getAttribute("user").equals("none")) {
		login.setVisible(false);
		logout.setVisible(true);
	    } else {
		logout.setVisible(false);
		login.setVisible(true);
	    }
	}
	VerticalLayout cpTab = (VerticalLayout) getSession().getAttribute("controlpanel");
	if ("admin".equals(getSession().getAttribute("user"))) {
	    cpTab.setVisible(true);
	    statLabel.setVisible(true);
	    ClientRunner.uiset.add(this);
	} else {
	    cpTab.setVisible(false);
	    statLabel.setVisible(false);
	    ClientRunner.uiset.remove(this);
	}
	VerticalLayout sTab = (VerticalLayout) getSession().getAttribute("search");
	if (!"none".equals(getSession().getAttribute("user"))) {
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
	VerticalLayout searchTab = null, controlPanelTab = null;

        layout.setMargin(true);
        setContent(layout);

	getSession().setAttribute("addr", request.getRemoteAddr());
	getSession().setAttribute("user", "none");
	initVars();
        
	HorizontalLayout topLine = new HorizontalLayout();
	Label topTitle = new Label("Aether disk search engine");
	topTitle.setWidth("90%");
	topLine.addComponent(topTitle);
	topLine.setHeight("10%");
	topLine.setWidth("100%");	
	boolean doauthenticate = (boolean) getSession().getAttribute("authenticate");
	statLabel = new Label();
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

	tabsheet.addTab(searchTab);
	// This tab gets its caption from the component caption
	tabsheet.addTab(controlPanelTab);
	//tabsheet.addTab(statTab);

	/*
	tabsheet.addTab(miscTab);
	tabsheet.addTab(comicsTab);
	tabsheet.addTab(trainingTab);
	*/

	HorizontalLayout bottomLine = new HorizontalLayout();
	bottomLine.setHeight("10%");
	bottomLine.setWidth("90%");
	Label dbLabel = new Label("Db type " + roart.util.Prop.getProp().getProperty("db"));
	//dbLabel.setWidth("30%");
	bottomLine.addComponent(dbLabel);
	Label idxLabel = new Label("Index type " + roart.util.Prop.getProp().getProperty("index"));
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
	tab.addComponent(getSearch("Search plain", 0));
	tab.addComponent(getSearch("Search analyzing", 1));
	tab.addComponent(getSearch("Search complex", 2));
	tab.addComponent(getSearch("Search extendable", 3));
	tab.addComponent(getSearch("Search multi", 4));
	tab.addComponent(getSearch("Search simple", 5));
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
	horInd.addComponent(getIndexSuffix());
	HorizontalLayout horReindex = new HorizontalLayout();
	horReindex.setHeight("20%");
	horReindex.setWidth("90%");
	horReindex.addComponent(getReindex());
	horReindex.addComponent(getReindexDateLower());
	horReindex.addComponent(getReindexDateHigher());
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

	/*
	tab.addComponent(getCleanup());
	tab.addComponent(getCleanup2());
	tab.addComponent(getCleanupfs());
	*/

	tab.addComponent(horNewInd);
	tab.addComponent(horNew);
	tab.addComponent(horInd);
	tab.addComponent(horReindex);
	tab.addComponent(horStat);
	tab.addComponent(horDb);
	return tab;
    }

    private Button getLogoutButton() {
        Button button = new Button("Logout");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		getSession().setAttribute("user", "none");
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
		    log.error("Exception", e);
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
		    log.error("Exception", e);
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
		    log.error("Exception", e);
		}
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
		    log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			maininst.index(value);
			Notification.show("Request sent");
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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
			log.error("Exception", e);
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

	boolean dodownload = (boolean) getSession().getAttribute("download");

	Table table = new Table("Table");
	table.setWidth("90%");
	int columns = strarr.get(0).get().size();
	for (int i=0; i<strarr.size(); i++) {
	    if (strarr.get(i).get().size() != columns) {
		System.out.println("column differs " + columns + " found " + strarr.get(i).get().size());
		break;
	    }
	}
	for (int i = 0; i < columns; i++) {
	    table.addContainerProperty(strarr.get(0).get().get(i), String.class, null);
	}
	if (dodownload) {
	    if (columns >= 2 && strarr.get(0).get().get(2).equals("Filename")) {
		table.addGeneratedColumn("Download", new ColumnGenerator() {
@Override
public Object generateCell(Table source, Object itemId,
			   Object columnId) {
    String filename = (String) source.getItem(itemId).getItemProperty("Filename").getValue();
    if (filename == null) {
	return null;
    }
    FileResource resource = new FileResource(new File(filename));
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
