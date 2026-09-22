package io.github.model;
import java.util.List;

public class Factor {
    private String name;
    private List<String> levels;

    public Factor(String name, List<String> levels){
        this.name = name;
        this.levels = levels;
    }

    public String getName(){
        return name;
    }

    public List<String>getLevels(){
        return levels;
   }
}
