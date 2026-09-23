import re

with open('app/src/main/java/com/online/flyer/design/postermaker/Leaflet_utils/Leaflet_SignatureFileUtils.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(
    r'long k = file1\.lastModified\(\) - file2\.lastModified\(\);\s*if \(k > 0\) return 1;\s*else if \(k == 0\) return 0;\s*else return -1;',
    r'return Long.compare(file1.lastModified(), file2.lastModified());',
    content
)

with open('app/src/main/java/com/online/flyer/design/postermaker/Leaflet_utils/Leaflet_SignatureFileUtils.java', 'w', encoding='utf-8') as f:
    f.write(content)
