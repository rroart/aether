package roart.client;

import roart.model.ResultItem;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Date;

//import roart.beans.session.misc.Unit;
import roart.beans.session.comic.Unit;
import roart.beans.session.comic.UnitBuy;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
	VerticalLayout searchTab = null, controlPanelTab = null, miscTab = null, comicsTab = null, trainingTab = null;

        layout.setMargin(true);
        setContent(layout);
        
	/*
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
	*/

	//        layout.addComponent(tf);

	Label topLine = new Label("Disk search engine");
	layout.addComponent(topLine);

	TabSheet tabsheet = new TabSheet();
	layout.addComponent(tabsheet);
	// Create the first tab
	searchTab = getSearchTab();
	// This tab gets its caption from the component caption
	controlPanelTab = getControlPanelTab();

	miscTab = getMiscTab();
	comicsTab = getComicsTab();
	trainingTab = getTrainingTab();

	tabsheet.addTab(searchTab);
	// This tab gets its caption from the component caption
	tabsheet.addTab(controlPanelTab);

	tabsheet.addTab(miscTab);
	tabsheet.addTab(comicsTab);
	tabsheet.addTab(trainingTab);
	
	HorizontalLayout bottomLine = new HorizontalLayout();
	bottomLine.addComponent(new Label("Db type " + roart.util.Prop.getProp().getProperty("mydb")));
	bottomLine.addComponent(new Label("Index type " + roart.util.Prop.getProp().getProperty("myindex")));
	bottomLine.addComponent(new Label("Affero GPL"));
	layout.addComponent(bottomLine);
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
	tab.addComponent(getFsIndexNew());
	tab.addComponent(getFsAddNew());
	tab.addComponent(getIndexNew());
	tab.addComponent(getFsIndexNewPath());
	tab.addComponent(getFsAddNewPath());
	tab.addComponent(getIndexNewPath());
	tab.addComponent(getNotIndexed());
	/*
	tab.addComponent(getCleanup());
	tab.addComponent(getCleanup2());
	tab.addComponent(getCleanupfs());
	*/
	tab.addComponent(getMemoryUsage());
	tab.addComponent(getOverlapping());
	tab.addComponent(getReindex());
	tab.addComponent(getReindexDate());
	tab.addComponent(getFsIndexNewMd5());
	tab.addComponent(getIndexSuffix());
	return tab;
    }

    private VerticalLayout getMiscTab() {
	VerticalLayout tab = new VerticalLayout();
	//tab.addComponent(tf2);
	tab.setCaption("Misc");
	HorizontalLayout cdTab = new HorizontalLayout(); 
	cdTab.addComponent(getMiscCreator("cd"));
	cdTab.addComponent(getMiscYear("cd"));
	cdTab.addComponent(getMiscSearch("cd"));
	HorizontalLayout dvdTab = new HorizontalLayout(); 
	dvdTab.addComponent(getMiscCreator("dvd"));
	dvdTab.addComponent(getMiscYear("dvd"));
	dvdTab.addComponent(getMiscSearch("dvd"));
	HorizontalLayout bookTab = new HorizontalLayout(); 
	bookTab.addComponent(getMiscCreator("book"));
	bookTab.addComponent(getMiscYear("book"));
	bookTab.addComponent(getMiscSearch("book"));
	HorizontalLayout book0Tab = new HorizontalLayout(); 
	book0Tab.addComponent(getMiscCreator("book0"));
	book0Tab.addComponent(getMiscYear("book0"));
	book0Tab.addComponent(getMiscSearch("book0"));
	HorizontalLayout bookuTab = new HorizontalLayout(); 
	bookuTab.addComponent(getMiscCreator("booku"));
	bookuTab.addComponent(getMiscYear("booku"));
	bookuTab.addComponent(getMiscSearch("booku"));
	tab.addComponent(cdTab);
	tab.addComponent(dvdTab);
	tab.addComponent(bookTab);
	tab.addComponent(book0Tab);
	tab.addComponent(bookuTab);
	return tab;
    }

    private VerticalLayout getComicsTab() {
	VerticalLayout tab = new VerticalLayout();
	//tab.addComponent(tf2);
	tab.setCaption("Comics");
	tab.addComponent(getComicTitles());
	tab.addComponent(getComicLetters());
	tab.addComponent(getComicAll());
	tab.addComponent(getComicSearch());
	tab.addComponent(getComicYear());
	return tab;
    }

    private VerticalLayout getTrainingTab() {
	VerticalLayout tab = new VerticalLayout();
	//tab.addComponent(tf2);
	tab.setCaption("Training");
	tab.addComponent(getTrainingYear());
	return tab;
    }

    private Button getFsIndexNew() {
        Button button = new Button("Index filesystem new items");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<String> strarr = null;
		try {
		    strarr = maininst.filesystemlucenenew();
		} catch (Exception e) {
		    log.error("Exception", e);
		}
		VerticalLayout result = getResultTemplate();
		addList(result, strarr);
		setContent(result);
            }
        });
	return button;
    }

    private Button getFsAddNew() {
        Button button = new Button("Filesystem add new");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<String> strarr = null;
		try {
		    strarr =maininst.traverse();
		} catch (Exception e) {
		    log.error("Exception", e);
		}
                VerticalLayout result = getResultTemplate();
                addList(result, strarr);
                setContent(result);
            }
        });
	return button;
    }

    private Button getIndexNew() {
        Button button = new Button("Index non-indexed items");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<String> strarr = null;
		try {
		    strarr = maininst.index(null);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
                VerticalLayout result = getResultTemplate();
                addList(result, strarr);
                setContent(result);
            }
        });
	return button;
    }

    private Button getMemoryUsage() {
        Button button = new Button("Memory usage");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<String> strarr = maininst.memoryusage();
                VerticalLayout result = getResultTemplate();
                addList(result, strarr);
                setContent(result);
            }
        });
	return button;
    }

    private Button getNotIndexed() {
        Button button = new Button("Get not yet indexed");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<List> lists = null;
		try {
		    lists = maininst.notindexed();
		} catch (Exception e) {
		    log.error("Exception", e);
		}
		List<ResultItem> strarr = lists.get(0);
		List<String> strarr2 = lists.get(1);
                VerticalLayout result = getResultTemplate();
                addListTable(result, strarr);
                addList(result, strarr2);
                setContent(result);
            }
        });
	return button;
    }

    private Button getOverlapping() {
        Button button = new Button("Overlapping");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		List<String> strarr = maininst.overlapping();
                VerticalLayout result = getResultTemplate();
                addList(result, strarr);
                setContent(result);
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.filesystemlucenenew(value, false);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.traverse(value);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.index(value, false);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.index(value, true);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private InlineDateField getReindexDate() {
	InlineDateField tf = new InlineDateField("Reindex on date");
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    long time = date.getTime();
		    List<String> strarr = null;
		    try {
			strarr = maininst.indexdate("" + time, true);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
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
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.index(value);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getFsIndexNewMd5() {
	TextField tf = new TextField("Filesystem index on md5");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.filesystemlucenenew(value, true);
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getMiscSearch(final String type) {
	TextField tf = new TextField("Search " + type);

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.misc.Main maininst = new roart.beans.session.misc.Main();
		    List<ResultItem> strarr = maininst.searchme(type, value);
		    VerticalLayout result = getResultTemplate();
		    addListTable(result, strarr);
		    setContent(result);
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
		    roart.beans.session.misc.Main maininst = new roart.beans.session.misc.Main();
		    List<ResultItem> strarr = maininst.searchme2(value, "" + type);
		    VerticalLayout result = getResultTemplate();
		    addListTable(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private ListSelect getMiscCreator(String type) {
	return getMiscYearOrCreator(type, "creator");
    }

    private ListSelect getMiscYear(String type) {
	return getMiscYearOrCreator(type, "year");
    }

    private ListSelect getMiscYearOrCreator(final String type, final String yc) {
	ListSelect ls = new ListSelect("Search " + type + " " + yc);
	// Add some items (here by the item ID as the caption)
	final roart.beans.session.misc.Main maininst = new roart.beans.session.misc.Main();
	if (yc.equals("year")) {
	    ls.addItems(maininst.getYears(type));
	    ls.setWidth("200");
	} else {
	    ls.addItems(maininst.getCreators(type));
	    ls.setWidth("400");
	}
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    List<roart.beans.session.misc.Unit> myunits = null;
		    if (yc.equals("year")) {
			myunits = maininst.searchyear(type, value);
		    } else {
			myunits = maininst.searchcreator(type, value);
		    }
		    Integer count = new Integer (0);
		    Float price = new Float (0);
		    Table table = new Table(type);
		    table.setWidth("800");
		    table.addContainerProperty("Date", String.class, null);
		    table.addContainerProperty("Count", String.class, null);
		    table.addContainerProperty("Type", String.class, null);
		    table.addContainerProperty("Price", String.class, null);
		    table.addContainerProperty("Creator", String.class, null);
		    table.addContainerProperty("Title", String.class, null);
		    if (type.startsWith("book")) {
			table.addContainerProperty("Isbn 1", Link.class, null);
			table.addContainerProperty("Isbn 2", Link.class, null);
			table.addContainerProperty("Isbn 3", Link.class, null);
			table.addContainerProperty("Isbn 4", Link.class, null);
		    }
		    for (int i=0; i<myunits.size(); i++) {
			count += new Integer(myunits.get(i).getCount());
			if (!myunits.get(i).getPrice().substring(0,1).equals("D") && !myunits.get(i).getPrice().substring(0,1).equals("L") && !myunits.get(i).getPrice().substring(0,1).equals("g") ) {
			    price += new Float(myunits.get(i).getPrice());
			}
			String isbn = myunits.get(i).getIsbn();
			String str = "";
			Link link1 = null, link2 = null, link3 = null, link4 = null;
			Object[] row;
			if (isbn != null && !isbn.equals("0")) {
			    link1 = new Link("US " + isbn, new ExternalResource("http://www.lookupbyisbn.com/Search/Book/" + isbn + "/1"));
			    link2 = new Link("US " + isbn, new ExternalResource("http://www.bookfinder.com/search/?st=sr&ac=qr&isbn=" + isbn));
			    link3 = new Link("SE " + isbn, new ExternalResource("http://libris.kb.se/hitlist?d=libris&q=numm%3a" + isbn));
			    link4 = new Link("G " + isbn, new ExternalResource("https://www.google.com/search?q=isbn%2b%2b" + isbn));
			    row = new Object[]{myunits.get(i).getDate(), myunits.get(i).getCount(), myunits.get(i).getType(), myunits.get(i).getPrice(), myunits.get(i).getCreator(), myunits.get(i).getTitle(), link1, link2, link3, link4};
			} else {
			    row = new Object[]{myunits.get(i).getDate(), myunits.get(i).getCount(), myunits.get(i).getType(), myunits.get(i).getPrice(), myunits.get(i).getCreator(), myunits.get(i).getTitle()};
			}

			table.addItem(row, i);
		    }
		    table.setPageLength(table.size());
		    VerticalLayout result = getResultTemplate();
		    result.addComponent(table);
		    result.addComponent(new Label("Size count price " + myunits.size() + " " + count + " " + price));
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    void addListTable(VerticalLayout ts, List<ResultItem> strarr) {
	Table table = new Table("Table");
	table.setWidth("800");
	int max = 0;
	for (int i=0; i<strarr.size(); i++) {
	    if (strarr.get(i).get().size() > max) {
		max = strarr.get(i).get().size();
	    }
	}
	for (int i = 0; i < max; i++) {
	    table.addContainerProperty(strarr.get(0).get().get(i), String.class, null);
	}
	for (int i = 1; i < strarr.size(); i++) {
	    ResultItem str = strarr.get(i);
	    table.addItem(str.getarr(), i);
	}
	table.setPageLength(table.size());
	ts.addComponent(table);
    }

    void addList(VerticalLayout ts, List<String> strarr) {
	for (int i=0; i<strarr.size(); i++) {
	    String str = strarr.get(i);
	    ts.addComponent(new Label(str));
	}
    }

    private ListSelect getComicTitles() {
	final ListSelect ls = new ListSelect("Search titles");
	// Add some items (here by the item ID as the caption)
	roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
        ls.addItems(maininst.getTitles("comic"));
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    int pos = -1;
		    int j = -1;
		    for (Object o : ls.getItemIds()) {
			j = j + 1;
			if(ls.isSelected(o)) {
			    if (pos < 0) {
				pos = j;
			    } else {
				log.error("double pos");
			    }
			}
		    }
		    roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
		    List<roart.beans.session.comic.Unit> myunits = maininst.searchtitle("comic", pos);
		    VerticalLayout result = getResultTemplate();
		    addListComic(result, myunits);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    private ListSelect getComicLetters() {
	ListSelect ls = new ListSelect("Search letters");
	// Add some items (here by the item ID as the caption)
	roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
	String[] items2 = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        ls.addItems(items2);
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    String letter = value;
		    roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
		    List<roart.beans.session.comic.Unit> myunits = maininst.searchtitle("comic", letter);
		    VerticalLayout result = getResultTemplate();
		    addListComic(result, myunits);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    private ListSelect getComicYear() {
	ListSelect ls = new ListSelect("Search years");
	// Add some items (here by the item ID as the caption)
	roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
	ls.addItems(maininst.getYears("com"));
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
		    Table table = new Table("Comics year 20" + value);
		    int count = 0;
		    int sum = 0;
		    String year = value;
		    TreeMap<String, Integer> mysums = new TreeMap<String, Integer>();
		    List<UnitBuy> myunits = maininst.searchyear(mysums, "com", year);

		    for (int i=0; i<myunits.size(); i++) {
			UnitBuy myunit = myunits.get(i);
			//String strcount = myunit.getCount();
			int prc = ((new Integer(myunit.getPriceInt())).intValue());
			sum += prc;
			table.addItem(new Object[]{myunits.get(i).getDate(), myunits.get(i).getPrice(), myunits.get(i).getData1() , ":", myunits.get(i).getData2()}, i);
			
		    }
		    table.setPageLength(myunits.size());
		    VerticalLayout result = getResultTemplate();
		    result.addComponent(table);
		    for (String key : mysums.keySet()) {
			Integer i = mysums.get(key);
			count += i.intValue();
			result.addComponent(new Label("key " + key + " " + i.intValue()));
		    }
		    result.addComponent(new Label("size count sum " + myunits.size() + " " + count + " " + sum));
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    private Button getComicAll() {
        Button button = new Button("Get all comics");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
		roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
		List<roart.beans.session.comic.Unit> myunits = maininst.searchtitle("comic");
                VerticalLayout result = getResultTemplate();
                addListComic(result, myunits);
                setContent(result);
            }
        });
	return button;
    }

    private TextField getComicSearch() {
	TextField tf = new TextField("Search comics");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    VerticalLayout result = getResultTemplate();
		    result.addComponent(new Label("unknown"));
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    void addListComic(VerticalLayout ts, List<roart.beans.session.comic.Unit> myunits) {
	//Table table = new Table(type);
	int count = 0;
        int sum = 0;
	for (int i=0; i<myunits.size(); i++) {
	    roart.beans.session.comic.Unit myunit = myunits.get(i);
	    String strcount = myunit.getCount();
	    int cnt = ((new Integer(strcount)).intValue());
	    int prc = ((new Integer(myunit.getPrice())).intValue());
	    count += cnt;
	    sum += cnt * prc;
	    ts.addComponent(new Label(myunits.get(i).getTitle()));
	    List<String> lines = myunits.get(i).getContent();
	    for (int j=0; j<lines.size(); j++) {
		ts.addComponent(new Label(lines.get(j)));
	    }
	}
	ts.addComponent(new Label("size count sum " + myunits.size() + " " + count + " " + sum));
	//table.setPageLength(myunits.size());
	//miscTab.addComponent(table);
	
    }

    private ListSelect getTrainingYear() {
	ListSelect ls = new ListSelect("Training years");
	// Add some items (here by the item ID as the caption)
	roart.beans.session.training.Main maininst = new roart.beans.session.training.Main();
	ls.addItems(maininst.getYears("tren"));
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    VerticalLayout result = getResultTemplate();
		    roart.beans.session.training.Main maininst = new roart.beans.session.training.Main();
		    int count = 0;
		    int sum = 0;
		    TreeMap<String, Integer> mysums = new TreeMap<String, Integer>();
		    String type = "tren";
		    String year = value;
		    List<roart.beans.session.training.Unit> myunits = maininst.searchyear(mysums, type, year);

		    for (int i=0; i<myunits.size(); i++) {
			roart.beans.session.training.Unit myunit = myunits.get(i);
			result.addComponent(new Label("t " + myunits.get(i).getDate() + " " + myunits.get(i).getData()));
		    }
		    for (String key : mysums.keySet()) {
			Integer i = mysums.get(key);
			count += i.intValue();
			result.addComponent(new Label("key " + i.intValue()));
		    }
		    result.addComponent(new Label("size count sum " + myunits.size() + " " + count + " " + sum));
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    private VerticalLayout getResultTemplate() {
	final Component content = getContent();
	VerticalLayout res = new VerticalLayout();
	res.addComponent(new Label("Search results"));
        Button button = new Button("Back to main page");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setContent(content);
            }
        });
	res.addComponent(button);
	return res;
    }

}
