function fileList = getImgFiles(dirName)

  dirData = dir(dirName);      %# Get the data for the current directory
  dirIndex = [dirData.isdir];  %# Find the index for directories
  fileList = {dirData(~dirIndex).name}';  %'# Get a list of the files
  if ~isempty(fileList)
    fileList = cellfun(@(x) fullfile(dirName,x),...  %# Prepend path to files
                       fileList,'UniformOutput',false);
                   
    % Filter out all non-images
    fileList = fileList(find(cellfun(@(x) size(x,1)>0, strfind(fileList, '.png'))));
  end
end