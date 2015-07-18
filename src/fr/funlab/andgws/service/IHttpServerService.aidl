package fr.funlab.andgws.service;
 
import fr.funlab.andgws.service.Data;
 
interface IHttpServerService {  
    int getPid(); // Renvoie le PID du processus du service
    Data getData(); // Renvoie notre objet mis à jour
    //void updateData(in Data data);
}
