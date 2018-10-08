package mx.com.juan.camacho.entidadesdb;
// Generated 2/10/2018 11:28:17 PM by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Blog generated by hbm2java
 */
public class Blog  implements java.io.Serializable {


     private int id;
     private Userapp userapp;
     private String title;
     private Date FCreate;
     private String description;
     private String content;
     private Set tests = new HashSet(0);

    public Blog() {
    }

	
    public Blog(int id, Userapp userapp, String title, String description, String content) {
        this.id = id;
        this.userapp = userapp;
        this.title = title;
        this.description = description;
        this.content = content;
    }
    public Blog(int id, Userapp userapp, String title, Date FCreate, String description, String content, Set tests) {
       this.id = id;
       this.userapp = userapp;
       this.title = title;
       this.FCreate = FCreate;
       this.description = description;
       this.content = content;
       this.tests = tests;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Userapp getUserapp() {
        return this.userapp;
    }
    
    public void setUserapp(Userapp userapp) {
        this.userapp = userapp;
    }
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getFCreate() {
        return this.FCreate;
    }
    
    public void setFCreate(Date FCreate) {
        this.FCreate = FCreate;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    public Set getTests() {
        return this.tests;
    }
    
    public void setTests(Set tests) {
        this.tests = tests;
    }




}


