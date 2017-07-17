package khaliliyoussef.bakingappforudacity;
import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * Created by Khalil on 7/1/2017.
 */
public class Debugging extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

    }
}
