#!/usr/bin/env python3
"""
Script to automatically replace android.util.Log with Logger in domain usecase files.
Part of Phase 1 architecture stabilization - Task #1

This script:
1. Replaces 'import android.util.Log' with 'import com.taskmanager.domain.logger.Logger'
2. Adds 'private val logger: Logger' parameter to class constructors
3. Replaces Log.d/i/w/e/v/wtf calls with logger.debug/info/warn/error/verbose/tf methods
"""

import os
import re
import sys
import shutil


def create_backup(filepath):
    """Create a backup of the file before modifying."""
    backup_path = filepath + '.bak'
    shutil.copy2(filepath, backup_path)
    return backup_path


def replace_log_in_file(filepath):
    """Replace android.util.Log with Logger in a single file."""
    try:
        # Create backup before modifying
        backup_path = create_backup(filepath)
        
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file uses android.util.Log
        if 'import android.util.Log' not in content:
            # Clean up backup if no changes needed
            os.remove(backup_path)
            return False
        
        print(f"Processing: {filepath}")
        
        # Replace import
        content = content.replace(
            'import android.util.Log',
            'import com.taskmanager.domain.logger.Logger'
        )
        
        # Improved: Check if logger is already in constructor or as a property
        # This regex matches 'private val logger' or 'val logger' or 'logger:' in constructor
        logger_pattern = r'(?:private\s+)?val\s+logger\s*[:=]|logger\s*:\s*Logger'
        if not re.search(logger_pattern, content):
            # Improved constructor pattern: matches class with constructor
            # Handles multi-line constructors and various formatting
            constructor_pattern = r'(\bclass\s+\w+[^\n]*\n[^\n]*\bconstructor\s*\()([^)]*)(\))'
            
            # Add logger parameter to constructor
            def add_logger_param(match):
                prefix = match.group(1)  # Everything before the opening paren
                params = match.group(2).strip()  # Existing parameters
                suffix = match.group(3)  # Closing paren
                
                # Handle empty parameters
                if not params:
                    new_params = 'private val logger: Logger'
                else:
                    # Add comma and logger parameter
                    new_params = params.rstrip(',') + ',\n        private val logger: Logger'
                
                return prefix + new_params + suffix
            
            content = re.sub(constructor_pattern, add_logger_param, content, flags=re.DOTALL)
        
        # Replace all Log calls (case-insensitive with optional whitespace)
        # Log.d -> logger.debug
        content = re.sub(r'\bLog\.\s*d\s*\(', 'logger.debug(', content, flags=re.IGNORECASE)
        # Log.i -> logger.info
        content = re.sub(r'\bLog\.\s*i\s*\(', 'logger.info(', content, flags=re.IGNORECASE)
        # Log.w -> logger.warn
        content = re.sub(r'\bLog\.\s*w\s*\(', 'logger.warn(', content, flags=re.IGNORECASE)
        # Log.e -> logger.error
        content = re.sub(r'\bLog\.\s*e\s*\(', 'logger.error(', content, flags=re.IGNORECASE)
        # Log.v -> logger.verbose
        content = re.sub(r'\bLog\.\s*v\s*\(', 'logger.verbose(', content, flags=re.IGNORECASE)
        # Log.wtf -> logger.wtf
        content = re.sub(r'\bLog\.\s*wtf\s*\(', 'logger.wtf(', content, flags=re.IGNORECASE)
        
        # Write the modified content
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        # Clean up backup on success
        os.remove(backup_path)
        
        print(f"Updated: {filepath}")
        return True
        
    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        # Keep backup on error
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
