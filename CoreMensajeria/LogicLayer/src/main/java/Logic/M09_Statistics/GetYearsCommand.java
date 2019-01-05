package Logic.M09_Statistics;

import Entities.Entity;
import Logic.Command;
import Persistence.DAOFactory;
import Persistence.M09_Statistics.DAOStatisticEstrella;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GetYearsCommand extends Command {

    private DAOStatisticEstrella dao;
    private ArrayList<Integer> years;

    @Override
    public void execute() throws Exception {
        dao = DAOFactory.instanciateDaoStatisticsEstrella();
        years = dao.getYears();
    }

    @Override
    public Entity Return() {
        return null;
    }

    public ArrayList<Integer> returnList() { return years; }
}