import re

with open('app/src/main/java/com/online/flyer/design/postermaker/Leaflet_utils/Leaflet_SignatureFileUtils.java', 'r', encoding='utf-8') as f:
    content = f.read()

new_method = '''public ArrayList<String> getFilePaths() {
        FILE_EXTN.add("png");
        FILE_EXTN.add("PNG");
        FILE_EXTN.add("jpg");
        FILE_EXTN.add("JPG");
        FILE_EXTN.add("jpeg");
        FILE_EXTN.add("JPEG");

        ArrayList<String> filePaths = new ArrayList<>();
        File directory = new File(Leaflet_MyApplication.getInstance().GetMainPath());

        if (directory != null && directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null) {
                Arrays.sort(listFiles, (file1, file2) -> {
                    long k = file1.lastModified() - file2.lastModified();
                    if (k > 0) return 1;
                    else if (k == 0) return 0;
                    else return -1;
                });
                for (File listFile : listFiles) {
                    if (IsSupportedFile(listFile.getAbsolutePath())) {
                        filePaths.add(listFile.getAbsolutePath());
                    }
                }
            }
        }
        return filePaths;
    }'''

content = re.sub(r'public ArrayList<String> getFilePaths\(\) \{.*?(?=private boolean IsSupportedFile)', new_method + '\n\n    ', content, flags=re.DOTALL)

with open('app/src/main/java/com/online/flyer/design/postermaker/Leaflet_utils/Leaflet_SignatureFileUtils.java', 'w', encoding='utf-8') as f:
    f.write(content)
