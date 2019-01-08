package Logic.M02_Company;

import Entities.Entity;
import Exceptions.M02_Company.CompanyNotFoundException;
import Exceptions.UnexpectedErrorException;
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
    public void execute() throws CompanyNotFoundException, UnexpectedErrorException {
        try {
            DAOCompany _dao = DAOFactory.instanciateDaoCompany ( );
           _co = _dao.update( _co );

        }catch(NullPointerException e) {
            throw new CompanyNotFoundException("Compañia no encontrada al Actualizar",e);
        }catch ( Exception e ){
            throw new UnexpectedErrorException( e );
        }

    }

    @Override
    public Entity Return() {
        return null;
    }

    //@Override
    public ArrayList<Entity> ReturnList() { return null ; }
}
