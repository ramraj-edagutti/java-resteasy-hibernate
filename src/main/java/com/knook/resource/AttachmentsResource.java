package com.knook.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("attachments")
@Produces(MediaType.APPLICATION_JSON)
public class AttachmentsResource {

    private GsonBuilder builder = new GsonBuilder()
        .setPrettyPrinting();
    private Gson gson = builder.create();

    private final String UPLOADED_FILE_PATH = "tmp/";

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        String fileName = uploadForm.get("fileName").get(0).getBodyAsString();
        List<InputPart> inputParts = uploadForm.get("attachment");

        for (InputPart inputPart : inputParts) {
            try {
                @SuppressWarnings("unused")
                MultivaluedMap<String, String> header = inputPart.getHeaders();

                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes, fileName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Response.status(200).entity("Uploaded file name : "+ fileName).build();
    }

    private void writeFile(byte[] content, String filename) throws IOException
    {
        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }

}