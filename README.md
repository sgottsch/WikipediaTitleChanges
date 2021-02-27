# Wikipedia Titles Changes

This tool can be used to track down changes of Wikipedia titles. Such information is not contained in the revision history of Wikipedia articles. Instead, the Wikipedia log files are used.

## Examples

- Back in 2011, what was the title of the Wikipedia article which was named ""Catherine, Duchess of Cambridge"" in 2017?
  - Kate Middleton
- Until 2016, which were the title changes of the Wikipedia article named "James Stewart" in 2006?
  - James Stewart (disambiguation) - 2007
  - James Stewart - 2009
  - James Stewart (disambiguation) - 2010

## Usage

### Data Download and Preprocessing

#### Log File Donwload

Follow the next lines to (1) download the latest logging file (English in this example), (2) unzip it, (3) extract the required logs and (4) clean the file.

```bash
wget https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-logging.xml.gz > logging.xml.gz
gunzip logging.xml.gz > logging.xml
grep '<type>move' -B 8 -A 4 logging.xml > actions.xml
grep -vE '^--$' actions.xml > actions02.xml
```

#### Preprocessing

Run the XMLLogReader to create a file with all title changes (first argument: log file, e.g. "actions02.xml", second argument: target file, e.g. "moves.csv").

### Querying

Use the TitleChangeApp (first argument: preprocessed file with all title changes, e.g. "moves.csv"). Example queries are given in the class.