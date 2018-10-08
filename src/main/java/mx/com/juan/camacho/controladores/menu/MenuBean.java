package mx.com.juan.camacho.controladores.menu;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import mx.com.juan.camacho.beans.GeneralBean;
import mx.com.juan.camacho.entidadesdb.Menu;

import java.util.List;

@ManagedBean
@SessionScoped
public class MenuBean extends GeneralBean {

  private List<Menu> itemsmenugroup1;

  public MenuBean() {
  }

  public List<Menu> getItemsmenugroup1() {
    try {
      if(this.itemsmenugroup1 == null) this.itemsmenugroup1 = this.dataSource.consultarLista("SELECT menu FROM Menu menu WHERE menu.groupId = 1");
    } catch(Exception e) {
    }
    return this.itemsmenugroup1;
  }

  public List<Menu> hijosDe(int idItemMenu) {
    List<Menu> hijos = null;
    try {
      hijos = this.dataSource.consultarLista("SELECT menu FROM Menu menu WHERE menu.menu.id = " + idItemMenu + " ORDER BY menu.groupId ASC");
    } catch(Exception e) {}
    return hijos;
  }

}
