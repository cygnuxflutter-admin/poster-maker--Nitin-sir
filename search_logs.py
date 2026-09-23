
import json
with open('C:/Users/Admin/.gemini/antigravity/brain/c4961892-37fe-4a61-b56b-b319d8600ea1/.system_generated/logs/transcript_full.jsonl', 'r', encoding='utf-8') as f:
    for line in f:
        obj = json.loads(line)
        if obj.get('source') == 'USER_EXPLICIT' and 'content' in obj:
            content = obj['content']
            if 'FATAL EXCEPTION' in content or 'Exception' in content or 'crash' in content.lower():
                idx = content.find('FATAL EXCEPTION')
                if idx == -1: idx = content.find('Exception')
                if idx == -1: idx = content.lower().find('crash')
                if idx != -1:
                    print('--- FOUND MATCH ---')
                    print(content[max(0, idx-500):idx+3000])

