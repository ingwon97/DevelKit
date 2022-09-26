package com.hanghae.final_project.image;



import org.junit.jupiter.api.*;

import org.springframework.boot.test.context.SpringBootTest;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageFileTest {
//    @Autowired
//    private S3UploaderService s3UploaderService;
//    private ArrayList<File> fileArrayList;
//    private  static MultipartFile[] multipartFileList;
//
//    @BeforeAll
//    void setup(){
//        multipartFileList=new MultipartFile[3];
//        for (int i = 0 ; i<3;i++) {
//            multipartFileList[i]= new MockMultipartFile(
//                    "Test_Image"+i,
//                    "Test_Image"+i+".png",
//                    MediaType.IMAGE_PNG_VALUE,
//                    "".getBytes());
//        }
//    }
//
//    @Nested
//    @DisplayName("FormData 이미지 파일 테스트")
//    class UploadFormDataImage{
//
//        @Test
//        @DisplayName("이미지 확장자 검사")
//        void checkImageFileExtension(){
//
//        }
//        @Test
//        @Order(1)
//        @DisplayName("이미지 파일 로컬 환경에 저장")
//        void setMultipartFileConvertToFile () throws IOException {
//
//            fileArrayList =s3UploaderService
//                    .convertFormDataImage(multipartFileList)
//                    .orElse(null);
//            Assertions.assertNotEquals(fileArrayList,null);
//        }
//        @Test
//        @Order(2)
//        @DisplayName("이미지 S3 업로드 후, 로컬 환경에서 삭제")
//        void deleteConvertedFileInLocalDirectory() {
//          for(File file: fileArrayList){
//              s3UploaderService.removeNewFile(file);
//              Assertions.assertFalse(file.exists());
//          }
//        }
//    }
}
