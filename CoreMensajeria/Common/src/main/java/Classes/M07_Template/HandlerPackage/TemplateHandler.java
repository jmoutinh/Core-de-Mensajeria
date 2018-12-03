package Classes.M07_Template.HandlerPackage;

import Classes.M01_Login.UserDAO;
import Classes.M03_Campaign.Campaign;
import Classes.M04_Integrator.IntegratorDAO;
import Classes.M06_DataOrigin.Application;
import Classes.M06_DataOrigin.ApplicationDAO;
import Classes.M07_Template.StatusPackage.Status;
import Classes.M07_Template.Template;
import Classes.Sql;
import Exceptions.MessageDoesntExistsException;
import Classes.M05_Channel.Channel;
import Classes.M05_Channel.ChannelFactory;
import Classes.M04_Integrator.Integrator;
import Classes.Sql;
import Exceptions.TemplateDoesntExistsException;
import com.google.gson.*;
import java.sql.*;
import java.util.ArrayList;

public class TemplateHandler {
    private Sql sql;

    private static final String GET_CAMPAIGN_BY_USER_OR_COMPANY =
        "select c.cam_id, c.cam_name, c.cam_description, c.cam_status, c.cam_start_date, c.cam_end_date,  co.com_id, co.com_name, co.com_description, co.com_status\n"
          + "from public.campaign c\n"
          + "inner join public.responsability r\n"
          + "on c.cam_company_id = r.res_com_id\n"
          + "inner join public.company co\n"
          + "on c.cam_company_id = co.com_id\n"
          + "where r.res_use_id = ? OR (r.res_use_id = ? AND r.res_com_id = ?)\n"
          + "order by c.cam_id";

    private static final String GET_CAMAPIGN_BY_ID =
            "select* from public.campaign where cam_id = ? ";

    public ArrayList<Template> getTemplates(int userId,int companyId){
        ArrayList<Template> templateArrayList = new ArrayList<>();
        ArrayList<Campaign> campaignArrayList = null;
        Connection connection = Sql.getConInstance();
        UserDAO userDAO = new UserDAO();
        try{
            campaignArrayList = getCampaignsByUserOrCompany(userId,companyId);
            for(int x = 0; x < campaignArrayList.size(); x++){
                PreparedStatement preparedStatement = connection.prepareCall("{call m07_select_templates_by_campaign(?)}");
                preparedStatement.setInt(1,campaignArrayList.get(x).get_idCampaign());
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    Template template = new Template();
                    template.setTemplateId(resultSet.getInt("tem_id"));
                    template.setCreationDate(resultSet.getString("tem_creation_date"));
                    Status status = Status.createStatus(resultSet.getInt("sta_id"),
                            resultSet.getString("sta_name"));
                    template.setStatus(status);
                    template.setChannels(getChannelsByTemplate(template.getTemplateId()));
                    template.setCampaign(campaignArrayList.get(x));
                    template.setApplication(getApplicationByTemplate(template.getTemplateId()));
                    template.setUser(userDAO.findByUsernameId(resultSet.getInt("tem_user_id")));
                    template.setMessage(MessageHandler.getMessage(template.getTemplateId()));
                    templateArrayList.add(template);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Sql.bdClose(connection);
            return templateArrayList;
        }
    }

    public ArrayList<Template> getTemplates(){
        ArrayList<Template> templateArrayList = new ArrayList<>();
        Connection connection = Sql.getConInstance();
        try{
            PreparedStatement preparedStatement = connection.prepareCall("{call m07_select_all_templates()}");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Template template = new Template();
                template.setTemplateId(resultSet.getInt("tem_id"));
                template.setCreationDate(resultSet.getString("tem_creation_date"));
                Status status = Status.createStatus(resultSet.getInt("sta_id"),
                        resultSet.getString("sta_name"));
                template.setStatus(status);
                template.setChannels(getChannelsByTemplate(template.getTemplateId()));
                template.setCampaign(getCampaingByTemplate(template.getTemplateId()));
                template.setApplication(getApplicationByTemplate(template.getTemplateId()));
                templateArrayList.add(template);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Sql.bdClose(sql.getConn());
            return templateArrayList;
        }
    }
    public Template getTemplate(int id) throws TemplateDoesntExistsException{
        Template template = new Template();
        String query = "select tem_id,ts_id,tem_user_id,tem_campaign_id, tem_creation_date, sta_name\n" +
                "from template_status,template,status\n" +
                "where tem_id = " + id + " and tem_id = ts_template and sta_id = ts_status\n" +
                "order by ts_id desc limit 1";
        try {
            ResultSet resultSet = sql.sqlConn(query);
            if (resultSet.next()) {
                //asignamos los datos basicos del propio template
                template.setTemplateId(resultSet.getInt("tem_id"));
                template.setCreationDate(resultSet.getString("tem_creation_date"));
                //asignamos el mensaje y status del template
                template.setMessage(MessageHandler.getMessage(template.getTemplateId()));
                template.setStatus(Status.createStatus(resultSet.getInt("ts_id"),
                        resultSet.getString("sta_name")));
                //asignamos canales, campaña y aplicacion
                template.setChannels(getChannelsByTemplate(template.getTemplateId()));
                template.setCampaign(getCampaingByTemplate(template.getTemplateId()));

                UserDAO userDAO = new UserDAO();
                template.setUser(userDAO.findByUsernameId(resultSet.getInt("tem_user_id")));
                template.setCampaign(getCampaignsById(resultSet.getInt("tem_campaign_id")));
                ApplicationDAO applicationService = new ApplicationDAO();
                template.setApplication(applicationService.getApplication
                        (template.getTemplateId()));
            }

        }catch (MessageDoesntExistsException e){
            e.printStackTrace();
        }catch(SQLException e){
            throw new TemplateDoesntExistsException
                    ("Error: la plantilla " + id + " no existe", e, id);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Sql.bdClose(sql.getConn());
            return template;
        }
    }

    public Campaign getCampaignsById(int campaignId){
        Campaign campaign =  new Campaign();
        Connection connection = Sql.getConInstance();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CAMAPIGN_BY_ID);
            preparedStatement.setInt(1,campaignId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                campaign.set_idCampaign(resultSet.getInt("cam_id"));
                campaign.set_nameCampaign(resultSet.getString("cam_name"));
                campaign.set_descCampaign(resultSet.getString("cam_description"));
                campaign.set_statusCampaign(resultSet.getBoolean("cam_status"));
                campaign.set_startCampaign(resultSet.getDate("cam_start_date"));
                campaign.set_endCampaign(resultSet.getDate("cam_end_date"));
            }
        } catch(SQLException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Sql.bdClose(connection);
            return campaign;
        }
    }

    public ArrayList<Campaign> getCampaignsByUserOrCompany(int userId, int companyId){
        ArrayList<Campaign> campaignArrayList = new ArrayList<>();
        Connection connection = Sql.getConInstance();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CAMPAIGN_BY_USER_OR_COMPANY);
            if((userId!=0)&&(companyId!=0)){
                preparedStatement.setInt(1,0);
                preparedStatement.setInt(2,userId);
                preparedStatement.setInt(3,companyId);
            }else if(userId!=0){
                preparedStatement.setInt(1,userId);
                preparedStatement.setInt(2,0);
                preparedStatement.setInt(3,0);
            }else{
                return null;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Campaign campaign = new Campaign();
                campaign.set_idCampaign(resultSet.getInt("cam_id"));
                campaign.set_nameCampaign(resultSet.getString("cam_name"));
                campaign.set_descCampaign(resultSet.getString("cam_description"));
                campaign.set_statusCampaign(resultSet.getBoolean("cam_status"));
                campaign.set_startCampaign(resultSet.getDate("cam_start_date"));
                campaign.set_endCampaign(resultSet.getDate("cam_end_date"));
                campaignArrayList.add(campaign);
            }
        }catch(SQLException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Sql.bdClose(connection);
            return campaignArrayList;
        }
    }

    public ArrayList<Channel> getChannelsByTemplate(int templateId){
        ArrayList<Channel> channels = new ArrayList<>();
        Connection connection = Sql.getConInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("select tci.tci_template_id, ci.ci_channel_id, ci.ci_integrator_id, \n"
                                    + "c.cha_name, cha_description\n"
                                    + "from channel_integrator ci\n"
                                    + "inner join template_channel_integrator tci\n"
                                    + "on tci.tci_ci_id = ci.ci_id\n"
                                    + "inner join channel c\n"
                                    + "on c.cha_id = ci.ci_channel_id\n"
                                    + "where tci.tci_template_id = " + templateId + "\n"
                                    + "order by ci.ci_channel_id;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                ArrayList<Integrator> integrators = new ArrayList<>();
                IntegratorDAO integratorDAO = new IntegratorDAO();
                Integrator integrator = integratorDAO.getConcreteIntegrator(
                        resultSet.getInt("ci_integrator_id")
                );
                integrators.add(integrator);
                Channel channel = new ChannelFactory().getChannel(
                        resultSet.getInt("ci_channel_id"),
                        resultSet.getString("cha_name"),
                        resultSet.getString("cha_description"),
                        integrators
                );
                channels.add(channel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            Sql.bdClose(connection);
            return channels;
        }
    }

    /**
     *
     * @param templateId
     * @return campaign
     *
     * Retorna una campana que tiene asociada la plantilla con el id = templateId
     */
    public Campaign getCampaingByTemplate(int templateId){
        Campaign campaign = new Campaign();
        try{
            //query que obtiene el id de la campana que tiene asociada la plantilla
            ResultSet resultSet = sql.sqlConn(
                    "SELECT tem_campaign_id\n"
                            + "FROM Template\n"
                            + "WHERE tem_id = " + templateId + ";");
            //instanciando el api de campana
            /* M03_Campaigns campaignsService = new M03_Campaigns();
            //obtener objeto campana con el id de campana del query anterior
            campaign = campaignsService.getDetails
                    (resultSet.getInt("tem_campaign_id"));*/
        } catch (SQLException e){
            e.printStackTrace();
            throw new TemplateDoesntExistsException
                    ("Error: la plantilla " + templateId + " no existe", e, templateId);
       /* } catch (CampaignDoesntExistsException e) {
            */
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (sql.getConn() != null) {
                Sql.bdClose(sql.getConn());
            }
            return campaign;
        }
    }

    /**
     *
     * @param templateId
     * @return application
     *
     * Retornar una aplicacion que tiene asociada la plantilla con el id = templateId
     */
    public Application getApplicationByTemplate(int templateId){
        Application application = new Application();
        try {
            //query que obtiene el id de la aplicacion que tiene asociada la plantilla
            ResultSet resultSet = sql.sqlConn(
                    "SELECT tem_application_id\n" +
                            "FROM Template\n" +
                            "WHERE tem_id = " + templateId + ";");
            //instanciado el api ApplicationDAO
            ApplicationDAO applicationService = new ApplicationDAO();
            //Obtener objeto aplicacion con el id de aplicacion del query anterior
            application = applicationService.getApplication
                    (resultSet.getInt("tem_application_id"));
        } catch (SQLException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (sql.getConn() != null) {
                Sql.bdClose(sql.getConn());
            }
            return application;
        }
    }

    public ArrayList<String> getTemplatePrivilegesByUser(int userId, int companyId){
        ArrayList<String> privileges = new ArrayList<>();
        Connection connection = Sql.getConInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareCall("{call m07_select_privileges_by_user_company(?,?)}");
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,companyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String privilege = resultSet.getString("pri_code");
                privileges.add(privilege);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return privileges;
    }


    public boolean postTemplateData(String json){
        try {
            Gson gson = new Gson();
            //recibimos el objeto json
            JsonParser parser = new JsonParser();
            JsonObject gsonObj = parser.parse(json).getAsJsonObject();
            //hay que extraer campaña y aplicacion, parametros por defecto
            //se crea el template y se retorna su id
            int templateId = postTemplate(gsonObj.get("campaign").getAsInt(),2, gsonObj.get("userId").getAsInt());
            //se establece el template  como no aprobado
            StatusHandler.postTemplateStatusNoAprovado(templateId);
            //insertamos los nuevos parametros
            String[] parameters = gson.fromJson(gsonObj.get("newParameters").getAsJsonArray(),String[].class);
            ParameterHandler.postParameter(parameters,gsonObj.get("company").getAsInt());
            //obtenemos el valor del mensaje,y parametros
            parameters = gson.fromJson(gsonObj.get("parameters").getAsJsonArray(),String[].class);
            String message = gsonObj.get("messagge").getAsString();
            MessageHandler.postMessage(message,templateId,parameters,gsonObj.get("company").getAsInt());

            //obtenemos los valores de los canales e integradores
            JsonArray channelIntegrator = gsonObj.get("channel_integrator").getAsJsonArray();
            postChannelIntegrator(channelIntegrator,templateId);

            return true;
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
    private void postChannelIntegrator(JsonArray channelIntegratorList,int templateId) {
        String query= "";
        JsonObject channelIntegrator;
        int channel;
        int integrator;
        sql = new Sql();
        try {
            for (JsonElement list : channelIntegratorList){
                channelIntegrator = list.getAsJsonObject();
                channel = channelIntegrator.get("channel").getAsJsonObject().get("idChannel").getAsInt();
                integrator = channelIntegrator.get("integrator").getAsJsonObject().get("idIntegrator").getAsInt();
                query = query + "insert into public.template_channel_integrator (tci_template_id,tci_ci_id) " +
                        "values (" + templateId + ",(select ci_id from public.channel_integrator " +
                        "where ci_channel_id = " + channel + " and ci_integrator_id = " + integrator +"));";
            }
            sql.sqlNoReturn(query);
        }catch (SQLException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            Sql.bdClose(sql.getConn());
        }
    }

    public int postTemplate(int campaignId,int applicationId, int userId){
        String query = "INSERT INTO public.Template (tem_creation_date, tem_campaign_id, tem_application_id, tem_user_id) \n" +
                "VALUES (CURRENT_DATE," + campaignId + "," + applicationId + "," + userId + ") RETURNING tem_id";
        int templateId=0;
        try{
            ResultSet resultSet = sql.sqlConn(query);
            if (resultSet.next())
                templateId=resultSet.getInt("tem_id");

        }catch (SQLException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            Sql.bdClose(sql.getConn());
            return templateId;
        }
    }

    public TemplateHandler() {
        sql = new Sql();
    }

    public Sql getSql() {
        return sql;
    }
}
