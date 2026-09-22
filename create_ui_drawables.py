import os

drawable_dir = r"d:\PRATIKSHA\poster-maker--Nitin-sir\app\src\main\res\drawable"
layout_dir = r"d:\PRATIKSHA\poster-maker--Nitin-sir\app\src\main\res\layout"

os.makedirs(drawable_dir, exist_ok=True)
os.makedirs(layout_dir, exist_ok=True)

drawables = {
    "bg_home_top_gradient.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:angle="315"
        android:endColor="#F5E6FF"
        android:startColor="#E6F0FF"
        android:type="linear" />
</shape>""",
    "bg_circle_white.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#FFFFFF" />
</shape>""",
    "bg_banner_gradient.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:angle="0"
        android:endColor="#4C1D95"
        android:startColor="#1E3A8A"
        android:type="linear" />
    <corners android:radius="20dp" />
</shape>""",
    "bg_button_blue.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#3B82F6" />
    <corners android:radius="20dp" />
</shape>""",
    "bg_icon_rounded.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF" />
    <corners android:radius="16dp" />
</shape>""",
    "bg_bottom_nav.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF" />
    <corners android:topLeftRadius="24dp" android:topRightRadius="24dp" />
    <stroke android:width="1dp" android:color="#F3F4F6"/>
</shape>""",
    "bg_circle_blue.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#2563EB" />
</shape>""",
    "ic_search_modern.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z" android:fillColor="#000000"/>
</vector>""",
    "ic_filter_modern.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M3,17v2h6v-2H3zM3,5v2h10V5H3zM13,21v-2h8v-2h-8v-2h-2v6H13zM7,9v2H3v2h4v2h2V9H7zM21,13v-2H11v2H21zM15,9h2V7h4V5h-4V3h-2V9z" android:fillColor="#000000"/>
</vector>""",
    "ic_create_poster.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M19,3H5C3.89,3 3,3.9 3,5v14c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V5C21,3.9 20.1,3 19,3zM19,19H5V5h14V19zM13.96,12.29l-2.75,3.54 -1.96,-2.36L6.5,17h11L13.96,12.29z" android:fillColor="#000000"/>
</vector>""",
    "ic_readymade_poster.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M12,3c-4.97,0 -9,4.03 -9,9s4.03,9 9,9c0.83,0 1.5,-0.67 1.5,-1.5 0,-0.39 -0.15,-0.74 -0.39,-1.01 -0.23,-0.26 -0.38,-0.61 -0.38,-0.99 0,-0.83 0.67,-1.5 1.5,-1.5H16c2.76,0 5,-2.24 5,-5C21,6.36 16.97,3 12,3zM6.5,12c-0.83,0 -1.5,-0.67 -1.5,-1.5S5.67,9 6.5,9 8,9.67 8,10.5 7.33,12 6.5,12zM9.5,8c-0.83,0 -1.5,-0.67 -1.5,-1.5S8.67,5 9.5,5 11,5.67 11,6.5 10.33,8 9.5,8zM14.5,8c-0.83,0 -1.5,-0.67 -1.5,-1.5S13.67,5 14.5,5 16,5.67 16,6.5 15.33,8 14.5,8zM17.5,12c-0.83,0 -1.5,-0.67 -1.5,-1.5S16.67,9 17.5,9 19,9.67 19,10.5 18.33,12 17.5,12z" android:fillColor="#000000"/>
</vector>""",
    "ic_my_creation.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M20,6h-8l-2,-2H4C2.9,4 2.01,4.9 2.01,6L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8C22,6.9 21.1,6 20,6zM20,18H4V8h16V18z" android:fillColor="#000000"/>
</vector>""",
    "ic_rate_us.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z" android:fillColor="#000000"/>
</vector>""",
    "ic_nav_home.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z" android:fillColor="#000000"/>
</vector>""",
    "ic_nav_templates.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M4,11h5V5H4V11zM4,19h5v-6H4V19zM11,19h9v-6h-9V19zM11,5v6h9V5H11z" android:fillColor="#000000"/>
</vector>""",
    "ic_nav_backgrounds.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M21,19V5c0,-1.1 -0.9,-2 -2,-2H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2zM8.5,13.5l2.5,3.01L14.5,12l4.5,6H5l3.5,-4.5z" android:fillColor="#000000"/>
</vector>""",
    "ic_nav_creations.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M20,6h-8l-2,-2H4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2zM20,18H4V8h16v10z" android:fillColor="#000000"/>
</vector>""",
    "ic_add_white.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
  <path android:pathData="M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" android:fillColor="#FFFFFF"/>
</vector>"""
}

for name, content in drawables.items():
    with open(os.path.join(drawable_dir, name), "w", encoding="utf-8") as f:
        f.write(content)

print("Generated drawables successfully.")
