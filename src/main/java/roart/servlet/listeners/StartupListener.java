package roart.servlet.listeners;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

public class StartupListener implements javax.servlet.ServletContextListener {

    public void contextInitialized(ServletContextEvent context)  {
	roart.beans.session.control.Main.parseconfig();
	roart.beans.session.misc.Main.parseconfig();
    }

    public void contextDestroyed(ServletContextEvent context) {
    }

}

