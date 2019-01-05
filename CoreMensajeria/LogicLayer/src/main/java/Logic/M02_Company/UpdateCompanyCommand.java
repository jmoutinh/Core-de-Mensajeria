package Logic.M02_Company;

import Entities.Entity;
import Logic.Command;
import Persistence.DAOFactory;
import Persistence.M02_Company.DAOCompany;

import java.util.ArrayList;

public class UpdateCompanyCommand extends Command {
    private static Entity _co;

    /**
     * Constructor de la clase.
     * @param _company instancia de la Compania que se desea actualizar
     */
    public  UpdateCompanyCommand ( Entity _company ){
        this._co = _company;
    }


    /**
     * Metodo que ejecuta la Accion del comando
     */
    @Override
    public void execute() throws Exception {
        try {
            DAOCompany _dao = DAOFactory.instanciateDaoCompany ( );
           _co = _dao.update( _co );
        }

        catch ( Exception e ){
            e.printStackTrace();
        }

    }

    @Override
    public Entity Return() {
        return null;
    }

    //@Override
    public ArrayList<Entity> ReturnList() { return null ; }
}
