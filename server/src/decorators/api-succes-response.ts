import { HttpStatus, Type, applyDecorators } from '@nestjs/common';
import { ApiExtraModels, ApiResponse, getSchemaPath } from '@nestjs/swagger';

export const ApiSuccessResponse = <TModel extends Type<any>>(
  statusCode: number,
  description: string,
  model?: TModel,
) => {
  return applyDecorators(
    ...[
      model && ApiExtraModels(model),
      ApiResponse({
        status: statusCode,
        description,
        schema: {
          properties: {
            statusCode: {
              type: 'number',
              example: statusCode,
            },
            message: {
              type: 'string',
              example: HttpStatus[statusCode],
            },
            ...(model && {
              data: {
                $ref: getSchemaPath(model),
              },
            }),
          },
        },
      }),
    ].filter(Boolean),
  );
};
