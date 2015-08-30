Place EVE SDE files here:

Files needed:
  'Blueprints.yaml':
     Download the Static Data Export .zip from the EVE Online Developers page 
     https://developers.eveonline.com/resource/static-data-export
     and extract the 'Blueprints.yaml' file in the 'sde' directory.
  'eve.db.bz2' or 'eve.db':
     Download the file 'eve.db.bz2' from https://www.fuzzwork.co.uk/dump/latest/ 
     and place it in the 'sde' directory.
  'Types/*_64,png' (Optional):
     Download the '*_Types.zip' file from EVE Online Developers page at
     https://developers.eveonline.com/resource/image-export-collection
     and extract its contents in the 'sde' directory.

The directory structure at the end should look like this:
  'datadump/'
  +- 'sde/'
  |  +- 'Blueprints.yaml'
  |  +- 'eve.db.bz2' (or 'eve.db.bz2')
  |  +- 'Types' (optional)
  |  |   +- '0_64.png'
  |  |   +- '1_64.png'
  |  |   +- '2_64.png'
  |  |   +- '3_64.png'
  |  |   +- ...
  +- 'datadump.py'
  +- ...
