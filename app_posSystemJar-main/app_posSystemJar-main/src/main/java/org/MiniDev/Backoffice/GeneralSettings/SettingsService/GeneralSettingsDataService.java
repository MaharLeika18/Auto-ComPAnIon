package org.MiniDev.Backoffice.GeneralSettings.SettingsService;

import org.MiniDev.Backoffice.GeneralSettings.SettingsModel.GeneralSettingsModel;
import org.MiniDev.Backoffice.GeneralSettings.SettingsRepo.GeneralSettingsRepo;


public class GeneralSettingsDataService {
    GeneralSettingsRepo repo = new GeneralSettingsRepo();

    public GeneralSettingsModel getBusinessDBData(){
        return repo.getGeneralSettingsModel();
    }
}
