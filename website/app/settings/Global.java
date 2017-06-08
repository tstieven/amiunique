/**
 * Created by thomas on 02/06/17.
 */


package settings;

import com.mongodb.DBCollection;
import play.Application;
import play.GlobalSettings;
import play.Logger;


public final class Global extends GlobalSettings {
    public static DBCollection collection;

    @Override
    public void onStart(Application app) {
        System.out.println("Debut du programme");
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }
}