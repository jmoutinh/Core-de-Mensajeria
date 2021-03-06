package DTO.M03_DTO;

import DTO.DTO;
import Entities.M02_Company.Company;

import java.util.Date;

public class DTOCampaignWithOut_Company extends DTO {

    private int _idCampaign;
    private String _nameCampaign;
    private String _descCampaign;
    private boolean _statusCampaign;
    private Date _startCampaign;
    private Date _endCampaign;
    private Company _company;

    /**
     * DTO para campañas sin Company
     * @param idCampaign  id de la campaña
     * @param nameCampaign nombre para una campaña recien creada
     * @param descCampaign descripcion de la campaña
     * @param statusCampaign el estatus de la campaña, puede ser activada o desactivada
     * @param startCampaign fecha de inicio de la campaña
     * @param endCampaign fecha de finalización de la campaña
     */
    public DTOCampaignWithOut_Company(int idCampaign, String nameCampaign, String descCampaign,
                                      boolean statusCampaign, Date startCampaign, Date endCampaign) {

        _idCampaign = idCampaign;
        _nameCampaign = nameCampaign;
        _descCampaign = descCampaign;
        _statusCampaign = statusCampaign;
        _startCampaign = startCampaign;
        _endCampaign = endCampaign;
    }

    public int get_idCampaign() {
        return _idCampaign;
    }

    public void set_idCampaign(int idCampaign) {
        _idCampaign = idCampaign;
    }

    public String get_nameCampaign() {
        return _nameCampaign;
    }

    public void set_nameCampaign(String nameCampaign) {
        _nameCampaign = nameCampaign;
    }

    public String get_descCampaign() {
        return _descCampaign;
    }

    public void set_descCampaign(String descCampaign) {
        _descCampaign = descCampaign;
    }

    public boolean get_statusCampaign() {
        return _statusCampaign;
    }

    public void set_statusCampaign(boolean statusCampaign) {
        _statusCampaign = statusCampaign;
    }

    public Date get_startCampaign() {
        return _startCampaign;
    }

    public void set_startCampaign(Date startCampaign) {
        _startCampaign = startCampaign;
    }

    public Date get_endCampaign() {
        return _endCampaign;
    }

    public void set_endCampaign(Date endCampaign) {
        _endCampaign = endCampaign;
    }



}
