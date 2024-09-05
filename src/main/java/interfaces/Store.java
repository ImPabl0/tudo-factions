package interfaces;

import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

public abstract class Store {

    protected abstract Map<String,Object> toMap();
    protected abstract void fromMap(Map<String,Object> map);

    private File getFile(){
        File file = new File("server.json");
        if(!file.exists()){
            try{
                if(file.createNewFile()){
                    return file;
                }else{
                    throw new Exception("Could not create file");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }

    public void save(){
        Gson gson = new Gson();
        String json = gson.toJson(toMap());
        File file = getFile();
        try{
            Files.write(file.toPath(),json.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void load(){
        File file = getFile();
        try{
            String json = new String(Files.readAllBytes(file.toPath()));
            Gson gson = new Gson();
            Map<String,Object> map = gson.fromJson(json,Map.class);
            fromMap(map);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
