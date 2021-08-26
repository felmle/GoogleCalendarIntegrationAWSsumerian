package function;

import com.google.api.client.util.store.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class MyMemoryFactory implements DataStoreFactory {
    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String filePath) throws IOException {
        DataStore<Serializable> dataStoreFile = new FileDataStoreFactory(new File("src/main/resources")).getDataStore("StoredCredential");

        DataStore<V> datastore = new MemoryDataStore<V>(this, "StoredCredential");

        for (String key : dataStoreFile.keySet()) {
            datastore.set(key, (V) dataStoreFile.get(key));
        }
        return datastore;
    }

    static class MemoryDataStore<V extends Serializable> extends AbstractMemoryDataStore<V> {
        MemoryDataStore(MyMemoryFactory dataStore, String id) {
            super(dataStore, id);
        }

        public MyMemoryFactory getDataStoreFactory() {
            return (MyMemoryFactory) super.getDataStoreFactory();
        }
    }
}
