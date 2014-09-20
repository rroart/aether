package roart.client;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
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
	tab.addComponent(getCleanup());
	tab.addComponent(getCleanup2());
	tab.addComponent(getCleanupfs());
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
	tab.addComponent(bookuTab);
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

    private TextField getNotIndexed() {
	TextField tf = new TextField("Get not yet indexed");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.notindexed();
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

    private TextField getMemoryUsage() {
	TextField tf = new TextField("Memory usage");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = maininst.memoryusage();
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private TextField getOverlapping() {
	TextField tf = new TextField("Overlapping");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = maininst.overlapping();
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
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

    private TextField getReindexDate() {
	TextField tf = new TextField("Reindex on date");

	// Handle changes in the value
	tf.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
		    List<String> strarr = null;
		    try {
			strarr = maininst.indexdate(value, true);
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
		    List<String> strarr = maininst.searchme(type, value);
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
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
		    List<String> strarr = maininst.searchme2(value, "" + type);
		    VerticalLayout result = getResultTemplate();
		    addList(result, strarr);
		    setContent(result);
		}
	    });
	// Fire value changes immediately when the field loses focus
	tf.setImmediate(true);
	return tf;
    }

    private ListSelect getMiscCreator(String type) {
	ListSelect ls = new ListSelect("Search " + type + " creator");
	// Add some items (here by the item ID as the caption)
	roart.beans.session.misc.Main maininst = new roart.beans.session.misc.Main();
        ls.addItems(maininst.getCreators(type));
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		}
	    });
	// Fire value changes immediately when the field loses focus
	ls.setImmediate(true);
	return ls;
    }

    private ListSelect getMiscYear(final String type) {
	ListSelect ls = new ListSelect("Search " + type + " year");
	// Add some items (here by the item ID as the caption)
	final roart.beans.session.misc.Main maininst = new roart.beans.session.misc.Main();
        ls.addItems(maininst.getYears(type));
	ls.setNullSelectionAllowed(false);
	// Show 5 items and a scrollbar if there are more
	ls.setRows(5);

	// Handle changes in the value
	ls.addValueChangeListener(new Property.ValueChangeListener() {
		public void valueChange(ValueChangeEvent event) {
		    // Assuming that the value type is a String
		    String value = (String) event.getProperty().getValue();
		    // Do something with the value
		    List<roart.beans.session.misc.Unit> myunits = maininst.searchyear(type, value);
		    Integer count = new Integer (0);
		    Float price = new Float (0);
		    Table table = new Table(type);
		    for (int i=0; i<myunits.size(); i++) {
			count += new Integer(myunits.get(i).getCount());
			if (!myunits.get(i).getPrice().substring(0,1).equals("D") && !myunits.get(i).getPrice().substring(0,1).equals("L") && !myunits.get(i).getPrice().substring(0,1).equals("g") ) {
			    price += new Float(myunits.get(i).getPrice());
			}
			String isbn = myunits.get(i).getIsbn();
			String str = "";
			if (isbn != null && !isbn.equals("0")) {
			    str = "<a href=\"http://www.lookupbyisbn.com/Search/Book/" + isbn + "\">US " + isbn + "</a><a href=\"http://www.ark.no/SamboWeb/sok.do?isbn=" + isbn + "\">NO " + isbn + "</a><a href=\"http://libris.kb.se/hitlist?d=libris&q=numm%3a" + isbn + "\">SE " + isbn + "</a><a href=\"https://www.google.com/search?q=isbn%2b%2b" + isbn + "\">G " + isbn + "</a>";

			}
			table.addItem(new Object[]{myunits.get(i).getDate(), myunits.get(i).getCount(), myunits.get(i).getType(), myunits.get(i).getPrice(), str, myunits.get(i).getCreator(), myunits.get(i).getTitle()}, i);
		    }
		    table.setPageLength(myunits.size());
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

    void addList(VerticalLayout ts, List<String> strarr) {
	for (int i=0; i<strarr.size(); i++) {
	    String str = strarr.get(i);
	    ts.addComponent(new Label(str));
	}
    }

    private ListSelect getComicTitles() {
	ListSelect ls = new ListSelect("Search titles");
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
		    roart.beans.session.comic.Main maininst = new roart.beans.session.comic.Main();
		    List<roart.beans.session.comic.Unit> myunits = maininst.searchtitle("comic", (new Integer(value)).intValue());
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
