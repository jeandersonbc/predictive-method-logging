#!/usr/bin/env Rscript
library(dplyr)
args <- commandArgs(trailingOnly=T)

if (length(args) == 0) {
    stop("Expected to read csv path from command-line")
}
csv_file <- args[1]

# Criteria
LOG_RATIO <- 4.0
PRODUCTION <- 100

df <- read.csv(csv_file)
df[is.na(df)] <- 0

baseline <- df %>% filter(name == "baseline")
baseline_name <- ""
baseline_defined = (nrow(baseline) == 1)
if (baseline_defined) {
    baseline_name <- baseline[1, "name"]
}
apache <- df %>% filter(name != baseline_name)

apache_before_sum <- apache %>%
    select(-c(name)) %>%
    sapply(sum)
apache_selected <- apache %>%
    filter(ratio > LOG_RATIO & production > PRODUCTION)
apache_after_sum <- apache_selected %>%
    select(-c(name)) %>%
    sapply(sum)

paste("Before filtering")
apache
paste("Projects:", dim(apache)[1])
apache_before_sum

paste("After filtering")
apache_selected
paste("Projects:", dim(apache_selected)[1])
apache_after_sum

write.csv(apache_selected, "filtering.csv", row.names=F)

paste("Summary")
apache_selected %>%
    select(-c(name, test, doc)) %>%
    summary

if (baseline_defined) {
    print("Baseline only")
    print(baseline)
    print("Total")
    print(apache_after_sum +
        (baseline %>% select(-c(name))))
}
