package com.carolinacomba.marketplace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImageUploadService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${imgbb.api.key:c77e996ba74855930d7f874d55c91004}")
    private String imgbbApiKey;
    
    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";
    
    public ImageUploadService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Sube una imagen a ImgBB y retorna la URL de la imagen
     * @param imageFile El archivo de imagen a subir
     * @return La URL de la imagen subida
     * @throws IOException Si hay error al procesar el archivo
     * @throws RuntimeException Si la subida falla
     */
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }
        
        // Validar tipo de archivo
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida");
        }
        
        // Validar tamaño del archivo (32MB máximo según ImgBB)
        if (imageFile.getSize() > 32 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo no puede exceder 32MB");
        }
        
        try {
            // Convertir imagen a base64
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Crear el cuerpo de la petición multipart
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("key", imgbbApiKey);
            builder.part("image", base64Image);
            
            // Realizar la petición a ImgBB
            String response = webClient.post()
                    .uri(IMGBB_UPLOAD_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Procesar la respuesta
            return processImgBBResponse(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Procesa la respuesta de ImgBB y extrae la URL de la imagen
     * @param response La respuesta JSON de ImgBB
     * @return La URL de la imagen
     * @throws RuntimeException Si la respuesta indica error
     */
    private String processImgBBResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Verificar si la petición fue exitosa
            boolean success = jsonNode.get("success").asBoolean();
            if (!success) {
                String errorMessage = jsonNode.has("error") ? 
                    jsonNode.get("error").get("message").asText() : 
                    "Error desconocido al subir la imagen";
                throw new RuntimeException("Error de ImgBB: " + errorMessage);
            }
            
            // Extraer la URL de la imagen
            JsonNode dataNode = jsonNode.get("data");
            if (dataNode == null || !dataNode.has("url")) {
                throw new RuntimeException("Respuesta inválida de ImgBB: no se encontró la URL de la imagen");
            }
            
            return dataNode.get("url").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta de ImgBB: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida si un archivo es una imagen válida
     * @param file El archivo a validar
     * @return true si es una imagen válida, false en caso contrario
     */
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * Obtiene el tamaño máximo permitido para archivos de imagen
     * @return El tamaño máximo en bytes
     */
    public long getMaxFileSize() {
        return 32 * 1024 * 1024; // 32MB
    }
}
