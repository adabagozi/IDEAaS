/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibs.bodai.ideaas.data_acquisition.webservice;

import java.util.Set;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
/**
*
* @author Ada
*/
@javax.ws.rs.ApplicationPath("requests")
public class ApplicationConfig extends Application {

   @Override
   public Set<Class<?>> getClasses() {
       Set<Class<?>> resources = new java.util.HashSet<>();
       addRestResourceClasses(resources);
       return resources;
   }

   /**
    * Do not modify addRestResourceClasses() method.
    * It is automatically populated with
    * all resources defined in the project.
    * If required, comment out calling this method in getClasses().
    */
   private void addRestResourceClasses(Set<Class<?>> resources) {
       resources.add(DataAcquisition.class);
       resources.add(MultiPartFeature.class);
//       resources.add(UploadFileService.class);
   }
   
}
