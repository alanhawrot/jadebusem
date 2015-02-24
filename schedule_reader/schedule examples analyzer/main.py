#!/usr/bin/python
__author__ = 'michal'
import os
import subprocess

ROOT_PATH = "./schedule examples"

def main():
    files_number=0
    correct_characters=0
    for item in os.listdir(ROOT_PATH):
        dir = os.path.join(ROOT_PATH, item)
        if os.path.isfile(dir):
            continue
        for root, dirs, files in os.walk(dir):
            print "parsing files in {}.".format(dir)
            for file in files:
                expected_value, sep, rest = file.partition('_')
                expected_value = expected_value.replace("dwukropek", ":")
                result = subprocess.check_output(["tesseract", root + "/" + file , "stdout", "-psm", "10"])
                if result.strip() != expected_value:
                    print "{}/{}: expected {} but got {}".format(root, file, expected_value, result.strip())
                else:
                    correct_characters += 1
                files_number+=1
    if files_number != 0:
        accuracy = (correct_characters * 100.0) / files_number
    else:
        accuracy = 100
    print "Accuracy: ({})%, {}/{} characters were correct.".format(accuracy, correct_characters, files_number)


main()