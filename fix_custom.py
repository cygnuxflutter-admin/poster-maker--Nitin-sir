import re

with open('app/src/main/res/layout/leaflet_activity_create_custom.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace root NestedScrollView to have fitsSystemWindows
content = re.sub(
    r'<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"',
    r'<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"\n    android:fitsSystemWindows="true"',
    content
)

# Replace the layout_height="0dp" and layout_weight="1" of the container LinearLayout
# We want the container LinearLayout to be wrap_content
content = re.sub(
    r'<!-- Card Container that expands to fill space -->\s*<LinearLayout\s+android:layout_width="match_parent"\s+android:layout_height="0dp"\s+android:layout_weight="1"',
    r'<!-- Card Container -->\n        <LinearLayout\n            android:layout_width="match_parent"\n            android:layout_height="wrap_content"',
    content
)

# Replace the cards to have a fixed height of 120dp instead of 0dp + weight 1
content = re.sub(
    r'android:layout_height="0dp"\s+android:layout_weight="1"',
    r'android:layout_height="120dp"',
    content
)

with open('app/src/main/res/layout/leaflet_activity_create_custom.xml', 'w', encoding='utf-8') as f:
    f.write(content)
