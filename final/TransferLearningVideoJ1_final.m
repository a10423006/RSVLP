alex = alexnet;
layers = alex.Layers

layers(23) = fullyConnectedLayer(36);
layers(25) = classificationLayer

allImages = imageDatastore('img', 'IncludeSubfolders', true, 'LabelSource', 'foldernames');
[trainingImages, testImages] = splitEachLabel(allImages, 0.9, 'randomize');

opts = trainingOptions('sgdm', 'InitialLearnRate', 0.001, 'MaxEpochs', 20, 'MiniBatchSize', 64);
final_Net = trainNetwork(trainingImages, layers, opts);

predictedLabels = classify(final_Net , testImages);
accuracy = mean(predictedLabels == testImages.Labels)