import { applyDecorators } from '@nestjs/common';
import { ApiExtraModels, ApiOkResponse, getSchemaPath } from '@nestjs/swagger';

export const CustomApiResponse = (
  responseClass: any,
  dataSchema: Record<string, any>,
) => {
  const models = Object.values(dataSchema);
  return applyDecorators(
    ApiExtraModels(responseClass, ...models),
    ApiOkResponse({
      schema: {
        allOf: [
          { $ref: getSchemaPath(responseClass) },
          {
            properties: {
              data: {
                type: 'object',
                properties: Object.keys(dataSchema).reduce(
                  (o, k, i) => ({
                    ...o,
                    [k]: { $ref: getSchemaPath(models[i]) },
                  }),
                  {},
                ),
              },
            },
          },
        ],
      },
    }),
  );
};
