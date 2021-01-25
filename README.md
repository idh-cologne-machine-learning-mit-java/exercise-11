# Exercise 11: Datavec


This exercise has the goal of creating a datavec pipeline to be used by deeplearning4j.

## Step 1
Please `clone` the repository `https://github.com/idh-cologne-machine-learning-mit-java/exercise-11`.

Create a new branch, using your UzK username.

As always, inspect the code provided to you. The `pom.xml` contains a lot of dependencies, please also have a look at them.

## Step 2
Understand and then run the main function in `Preprocess` (might take a few minutes). The UIMA pipeline reads in IMDB movie reviews from `src/main/resources/imdb`, runs a (DKpro) tokenizer and (DKpro) part of speech tagger. The last component in the pieline is `CSVWriter`, a custom file writer. The writer counts part of speech frequencies per file, and prints them into a CSV file.

### 2.1 (optional)
Extend the CSVWriter class, such that you *also* (in addition to the pos frequencies) write word counts into the file. Please count each word from `src/main/resources/vocabulary.txt` (which contains the 200 most frequent words from the Sherlock Holmes stories) and add them to the CSV file (imagine these being sentiment or emotion words!).

## Step 3
The file `Exercise11Main` contains a `main()` function that reads in data from the file that you have created in step 2. 

Please add two kinds of transforms:

1. Add a transform class to remove all pos tags *except* the verb tags (i.e., VBG, VBN, VBZ, VBP, VBD, VB, NN, NNS). This can be done with `org.datavec.api.transform.transform.column.RemoveColumnsTransform` or `org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform`. 
2. Add a transform to scale the values of the VBG column into the range between 0 and 1. To this end, you'll need to use the transform class `org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer.MinMaxNormalizer` and supply the minimal and maximal values from the data set as arguments. While the lower value is obviously 0 (zero), finding out the highest number of VBG tags in a document is tricky (in Weka, this kind of operation led a filter to being "supervised"), because we need the data set to find out about this. You can of course write a Python/Perl/Shell script to find this value, but within Datavec, you can use the static function `AnalyzeLocal.analyze(...)` to get this value.

Both transforms need to be plugged into a TransformProcess. To create this, you'll need to have a schema for the dataset. You can use the class `InferredSchema` to infer the schema from the actual data set.

Use the transform process in a `TransformProcessRecordReader`, and adapt the while loop to use the new record reader.
After having done everything, the beginning of the output should look like this:

```
[2, 0.018518518518518517, 2, 0, 12, 11, 3, 1]
[1, 0.018518518518518517, 1, 1, 5, 2, 1, 3]
[1, 0.018518518518518517, 6, 1, 7, 0, 0, 4]
...
```

## Step 4
As always, commit and push your code.