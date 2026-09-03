#!/usr/bin/env python3
"""
Script to automatically replace android.util.Log with Logger in domain usecase files.
Part of Phase 1 architecture stabilization - Task #1
"""

import os
import re
import sys

def replace_log_in_file(filepath):
    """Replace android.util.Log with Logger in a single file."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file uses android.util.Log
        if 'import android.util.Log' not in content:
            return False
        
        print(f"Processing: {filepath}")
        
        # Replace import
        content = content.replace(
            'import android.util.Log',
            'import com.taskmanager.domain.logger.Logger'
        )
        
        # Find the class and its constructor
        # Pattern to match constructor with parameters
        constructor_pattern = r'(class\s+\w+[^\n]*\n[^\n]*constructor\s*\(\s*([^)]*)\)'
        
        # Check if logger is already in constructor
        if 'logger:' not in content.lower() and 'private val logger' not in content:
            # Add logger parameter to constructor
            content = re.sub(
                constructor_pattern,
                lambda m: f'{m.group(1)}\n    , private val logger: Logger',
                content
            )
        
        # Replace Log calls
        content = re.sub(r'\bLog\.d\(', 'logger.debug(', content)
        content = re.sub(r'\bLog\.i\(', 'logger.info(', content)
        content = re.sub(r'\bLog\.w\(', 'logger.warn(', content)
        content = re.sub(r'\bLog\.e\(', 'logger.error(', content)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"Updated: {filepath}")
        return True
        
    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        return False

def main():
    """Main function to process all usecase files."""
    base_path = 'app/src/main/java/com/taskmanager/domain/usecase'
    
    if not os.path.exists(base_path):
        print(f"Error: Base path not found: {base_path}")
        print("Please run this script from the project root directory.")
        sys.exit(1)
    
    # Walk through all usecase directories
    updated_count = 0
    for root, dirs, files in os.walk(base_path):
        for file in files:
            if file.endswith('.kt'):
                filepath = os.path.join(root, file)
                if replace_log_in_file(filepath):
                    updated_count += 1
    
    print(f"\nCompleted: {updated_count} files updated")
    
    # Verify changes
    print("\nVerifying changes...")
    log_files = []
    for root, dirs, files in os.walk(base_path):
        for file in files:
            if file.endswith('.kt'):
                filepath = os.path.join(root, file)
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                    if 'import android.util.Log' in content:
                        log_files.append(filepath)
    
    if log_files:
        print(f"Warning: {len(log_files)} files still contain android.util.Log:")
        for f in log_files:
            print(f"  - {f}")
    else:
        print("Success: No files contain android.util.Log in domain/usecase")

if __name__ == '__main__':
    main()
